package com.salabon.lazycards.Cards;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salabon.lazycards.Cards.Constants.Anki;
import com.salabon.lazycards.Cards.Dialogs.ServerErrorDialog;
import com.salabon.lazycards.Cards.Services.DeckService;
import com.salabon.lazycards.Cards.Services.ServerCommService;
import com.salabon.lazycards.Database.CardDbManager;
import com.salabon.lazycards.R;

import java.util.List;

public class QueuedCardsFragment extends Fragment {

    private static final String SERVER_ERROR_DIALOG = "server_error_dialog";

    private CardDbManager mCardDbManager = CardDbManager.getInstance(getActivity());
    private Button mSubmitAllButton;
    private RecyclerView mRecyclerView;
    private QueuedCardAdapter mAdapter;
    private QueuedCardUploader mQueuedCardUploader;
    private String[] mApiText;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
        View v = inflater.inflate(R.layout.fragment_queued_cards, container, false);

        mApiText = getActivity().getResources().getStringArray(R.array.apis);

        mRecyclerView = v.findViewById(R.id.queued_card_recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        DividerItemDecoration horizontalDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                DividerItemDecoration.VERTICAL);
        Drawable horizontalDivider = ContextCompat.getDrawable(getActivity(), R.drawable.ic_divider_grey);
        horizontalDecoration.setDrawable(horizontalDivider);
        mRecyclerView.addItemDecoration(horizontalDecoration);

        mQueuedCardUploader = new QueuedCardUploader(getActivity());
        mQueuedCardUploader.start();
        mQueuedCardUploader.getLooper();

        mSubmitAllButton = v.findViewById(R.id.submit_all_button);
        mSubmitAllButton.setOnClickListener((listener) ->{
            mQueuedCardUploader.submitCard();
        });

        updateUI();

        return v;
    }

    // TODO TEST THIS OUT
    private void updateUI(){
        List<Card> cards = mCardDbManager.getCards();

        if(mAdapter == null){
            mAdapter = new QueuedCardAdapter(cards);
            mRecyclerView.setAdapter(mAdapter);
        }
        else{
            mAdapter.setCards(cards);
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        IntentFilter filter = new IntentFilter(DeckService.ACTION_FINISHED);
        getActivity().registerReceiver(mOnDeckServiceFinished, filter, DeckService.PERM_PRIVATE, null);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(mOnDeckServiceFinished);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        mQueuedCardUploader.quit();
    }

    // TODO create an abstract fragment that has this
    // Success should be an abstract method
    private BroadcastReceiver mOnDeckServiceFinished = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int status = intent.getIntExtra(ServerCommService.ACTION_STATUS, 0);

            if(status == Anki.ActionResult.SUCCESS){
                mAdapter.removeFirstCard();
                mQueuedCardUploader.submitCard();
                // request for a new card to be uploaded
            }
            else{
                FragmentManager fragmentManager = getFragmentManager();
                ServerErrorDialog dialog;
                // These handle the custom messages received from either the currently selected API
                // or AnkiConnect
                if(status == Anki.ActionResult.ANKI_CONNECT_ERROR
                        || status == Anki.ActionResult.API_ERROR){
                    String body = intent.getStringExtra(ServerCommService.ERROR_BODY);
                    dialog = ServerErrorDialog.newInstance(status, body);
                }
                else{
                    dialog = ServerErrorDialog.newInstance(status);
                }
                dialog.show(fragmentManager, SERVER_ERROR_DIALOG);
            }
        }
    };

    private class CardHolder extends RecyclerView.ViewHolder{
        private TextView mVocabWord;
        private TextView mDeck;
        private TextView mApi;
        private TextView mApiStatic;
        private TextView mDeckStatic;

        private Card mCard;

        CardHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.list_item_queued_card, parent, false));

            mVocabWord = itemView.findViewById(R.id.queued_vocab_word);
            mDeckStatic = itemView.findViewById(R.id.queued_card_deck_static);
            mDeck = itemView.findViewById(R.id.queued_card_deck_user_selected);
            mApi = itemView.findViewById(R.id.queued_card_api_user_selected);
            mApiStatic = itemView.findViewById(R.id.queued_card_api_static);
        }

        private void bind(Card card){
            mCard = card;

            mVocabWord.setText(card.getVocabWord());
            mDeck.setText(card.getDeck());
            mApi.setText(mApiText[card.getApi()]);
        }
    }

    private class QueuedCardAdapter extends RecyclerView.Adapter<CardHolder>{
        List<Card> mCards;

        QueuedCardAdapter(List<Card> cards) { mCards = cards; }
        @NonNull
        @Override
        public CardHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            return new CardHolder(inflater, parent);
        }

        @Override
        public void onBindViewHolder(@NonNull CardHolder holder, int position) {
            Card card = mCards.get(position);
            holder.bind(card);
        }

        @Override
        public int getItemCount() {
            return mCards.size();
        }

        private void setCards(List<Card> cards) { mCards = cards; }

        private Card getItem(int pos){
            return mCards.get(pos);
        }

        private void removeFirstCard(){
            mCardDbManager.deleteCard(mAdapter.getItem(0));
            mCards.remove(0);
            notifyItemRemoved(0);
        }
    }
}
