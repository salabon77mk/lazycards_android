# LazyCards

An Android app that creates vocabulary flash cards for you! No need to 
create a list of words to look up, waste time copy/pasting definitions, antonyms, whatever
into Anki. Just enter your word, select the deck Anki Deck you want to upload to, select 
what you'd like to include in your definition, and submit it away.

## REQUIREMENTS

* [Anki](https://apps.ankiweb.net/) installed on a Linux/Windows/MacOS
* [AnkiConnect](https://ankiweb.net/shared/info/2055492159)
* [LazyCards Server](https://github.com/salabon77mk/lazycards_server)
* Android device (for the app!)

### First Time Use

(Temporary) On the homescreen, press the NET\_SCAN button, and either select a device or input the
IP address of the device that is hosting the LazyCards Server.

### APIs currently supported

[WordsAPI](https://www.wordsapi.com/)


### Demo
1. Input a word
![enter word](../pictures/lazycards_demo/01_enter_word.jpg)

2. Select your deck
![select deck](../pictures/lazycards_demo/02_select_deck.jpg)

3. Choose the options you'd like to include as part of your flashcard
![options](../pictures/lazycards_demo/03_select_options.jpg)

4. Submit your word!
![submission success](../pictures/lazycards_demo/04_submit_success.jpg)

5. Check out Anki. A new word!
![new card](../pictures/lazycards_demo/05_new_card.png)

6. Here's the definition with the options you selected. Looks like the API couldn't find antonyms
for taste
![definition](../pictures/lazycards_demo/06_card_definition.png)


### Network Scanner Demo
1. The scan just started!
![start scan](../pictures/lazycards_demo/net_scan_demo/01_start_scan.jpg)

2. Getting some results
![partial results list](../pictures/lazycards_demo/net_scan_demo/02_some_results.jpg)

3. Scan complete and results sorted via Key-Index sort
![full results](../pictures/lazycards_demo/net_scan_demo/03_scan_complete.jpg)

4. Select your host and your port optionally as well too!
![host select](../pictures/lazycards_demo/net_scan_demo/04_host_select.jpg)
