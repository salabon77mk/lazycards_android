package com.salabon.lazycards.Cards;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.salabon.lazycards.Database.CardDbManager;
import com.salabon.lazycards.R;

import java.util.List;

public class QueuedCardsFragment extends Fragment {

    private CardDbManager mCardDbManager = CardDbManager.getInstance(getActivity());
    private RecyclerView mRecyclerView;
    private QueuedCardAdapter mAdapter;
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
    }
}
