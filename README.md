# LazyCards

An Android app that creates vocabulary flash cards for you! No need to 
create a list of words to look up, waste time copy/pasting definitions, antonyms, whatever
into Anki. Just enter your word, select the deck Anki Deck you want to upload to, select 
what you'd like to include in your definition, and submit it away.

## REQUIREMENTS

* [Anki](https://apps.ankiweb.net/) installed on a Linux/Windows/MacOS desktop
* [AnkiConnect](https://ankiweb.net/shared/info/2055492159), an addon for Anki
* [LazyCards Server](https://github.com/salabon77mk/lazycards_server)
* Android device (for the app!)

## First Time Use

Clone this repository and import it into Android Studio as a project. From there, connect your phone and hit run.
The app is now installed.

Press the navigation icon at the top left, click Network Scanner, and either wait for the scan
to complete and select a device or input the IP address and port of the LazyCards Server.

## APIs currently supported

[WordsAPI](https://www.wordsapi.com/)


## DEMOS

### Submit Word Demo
1. Input a word. The first text box is the front of the card. The second is your own note for the back.
<br/><br/>
![enter word](./demo_imgs/01_fill_out_word.jpg)
<br/><br/>

2. Select your deck
<br/><br/>
![select deck](./demo_imgs/02_select_deck.jpg)
<br/><br/>

3. Select the API you would like to use
<br/><br/>
![select api](./demo_imgs/03_select_api.jpg)
<br/><br/>

4. Choose the options you'd like to include as part of your flashcard
<br/><br/>
![options](./demo_imgs/04_select_word_options.jpg)
<br/><br/>

5. Submit your word! 
<br/><br/>
![submission success](./demo_imgs/05_success.jpg)
<br/><br/>

6. Check out Anki. A new word!
<br/><br/>
![new card](./demo_imgs/06_new_card.png)
<br/><br/>

7. Here's the definition with the options you selected. Looks like the API couldn't find antonyms
for taste
<br/><br/>
![definition](./demo_imgs/07_the_word_in_anki.png)
<br/><br/>

---

### Queue Demo, What happens when you're away from home and have a bunch of new cards to send?

1. After filling out the details of your word, check "Send to queue instead" and hit "Submit"
<br/><br/>
![check queue](./demo_imgs/queue/01_send_words_to_queue.jpg)
<br/><br/>

2. After sending some cards to the queue, click on the navigation icon at the top left and select
"Queued"
<br/><br/>
![nav](./demo_imgs/navigation.jpg)
<br/><br/>

3. A queue with some cards waiting to be sent to Anki
<br/><br/>
![check queue](./demo_imgs/queue/02_queue_with_cards.jpg)
<br/><br/>

4. Hit the submit button and watch the cards get sent away! The below image is once all cards 
are sent.
<br/><br/>
![all send](./demo_imgs/queue/03_success_sent_all_cards.jpg)
<br/><br/>

5. Three new cards have appeared!
<br/><br/>
![many new cards](./demo_imgs/queue/04_many_new_cards_anki.png)
<br/><br/>

6. Here they are in Anki's card browser
<br/><br/>
![new cards in anki browse](./demo_imgs/queue/05_here_are_the_cards.png)
<br/><br/>


---
### Network Scanner Demo
1. Navigate to the Network Scanner
<br/><br/>
![navigation](./demo_imgs/navigation.jpg)
<br/><br/>

2. The scan just started!
<br/><br/>
![start scan](./demo_imgs/net_scan_demo/01_start_scan.jpg)
<br/><br/>

3. Getting some results
<br/><br/>
![partial results list](./demo_imgs/net_scan_demo/02_some_results.jpg)
<br/><br/>

4. Scan complete and results sorted via Key-Index sort
<br/><br/>
![full results](./demo_imgs/net_scan_demo/03_scan_complete.jpg)
<br/><br/>

5. Select your host and your port optionally as well too!
<br/><br/>
![host select](./demo_imgs/net_scan_demo/04_host_select.jpg)
<br/><br/>
