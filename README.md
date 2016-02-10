# aQuery
*jQuery for Android*

* Demo version : http://timothe.malahieude.net/projects/aQuery/aQuery-demo.zip
* Demo APK : http://timothe.malahieude.net/projects/aQuery/aQuery-demo.apk
* Minimal example : http://timothe.malahieude.net/projects/aQuery/aQuery-min.zip

aQuery is basically the Android equivalent of the famous Web Framework [jQuery](https://jquery.com/).
Its aim is exactly the same : perform basic tasks in few lines of code. You will find the "magic" `$` function that does everything, and most of the functions available in jQuery Framework can be found in aQuery too : `elt.prop()`, `elt.attr()`, `$.ajax()`... All of these are implemented and work the same way !

## Comparison with/without
Here is a code example that does not use aQuery :
```java
LinearLayout msgsContainer = (LinearLayout) findViewById(R.id.msgs_container);

TextView newMsg = new TextView(this);
int msgHeight = Math.round(25 * getResources().getDisplayMetrics().density);
newMsg.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, msgHeight));
newMsg.setTextColor(getResources().getColor(R.color.msg_color));
newMsg.setGravity(Gravity.CENTER);
EditText msgInput = (EditText) findViewById(R.id.msg_input);
newMsg.setText(msgInput.getText());
msgsContainer.addView(newMsg);

msgInput.setText("");
InputMethodManager im = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
im.hideSoftInputFromWindow(msgInput.getWindowToken(), 0);
```
Now here is a code equivalent, using aQuery :
```java
AQuery newMsg = $.create("TextView");
newMsg.lp("match_parent", "25dp")
	.prop("textColor", $.color(R.color.msg_color))
	.attr("gravity", "center")
	.text($("#msg_input").val());
$("#msgs_container").append(newMsg);
$("#msg_input").val("").hideKeyboard();
```
It's better, isn't it ? aQuery will save you time and make your code cleaner.

## Features overview
### Accessing elements
aQuery provides a powerful tool to access elements and get/set their properties :
* Getting an element with id `R.id.my_id` : `$("#my_id")`
* Getting all ImageViews inside a LinearLayout : `$("LinearLayout ImageView")`
* Getting 1 TextView over 2 in a ListView : `$("ListView TextView:nth-child(2n)")`
* More generally, all CSS selectors supported by jQuery are supported by aQuery.

* Acessing any element property : `element.propi("gravity");`
* Setting any element property by direct value : `element.prop("gravity", Gravity.CENTER);`
* Setting any element property by XML value : `element.attr("gravity", "center");`

### AJAX queries
With aQuery, performing http requests will be incredibly easy. Here is an example :
```java
$.get("http://myapi.com/profile/", new NetworkListener() {
	@Override
	public void done(String res) {
      // Response obtained successfully
	}
	@Override
	public void fail(Exception e) {
      // Something went wrong
	}
	@Override
	public void always(boolean success) {
      // Function always triggered, error or not
	}
});
```

### Animations
Animations couldn't be easier. Great stuff can be done in very few lines of code !
* Hiding an element progressively : `element.hide(1000);` or `element.fadeOut(1000);`
* Changing an element property on touch : `element.hover(Transition.prop("textColor", "#F80"), 300);`
* Performing simple animations... : `element.animate(Transition.prop("background", "#33B5E5"), 1000);`
* ... Or more complicated ones :
```java
element.animate(new PropertyTransition[] {
    Transition.attr("layout_height", "wrap_content"),
    Transition.attr("layout_margin", "5dp"),
    Transition.prop("alpha", 1)
}, 300, "easeInSine", new CompleteListener() {
	@Override
	public void complete(View v) {
      // Animation is complete
	}
});
```
* Chain animations : `element.animate(Transition.attr("textColor", "blue")).animate(Transition.attr("textColor", "red"));`
* Animating after a delay : `element.delay(500).animate(Transition.prop("alpha", 1));`

### Dialog boxes
User interaction is the basis of all applications. That's why aQuery proposes some useful methods to show simple dialog boxes.
* Display an informative box : `$.inform("Title", "Message");`
* Ask the user to confirm an action :
```java
$.confirm("Delete message ?", "Warning, this operation is irreversible", new Runnable() {
    @Override
    public void run() {
        // User confirmed
    });
```
* Ask the user to enter a text :
```java
$.prompt("Identity control", "Please enter your name", new PromptListener() {
    @Override
    public void onSubmit(String value) {
        // User submitted the dialog box
    }
    @Override
    public void onCancel() {
        // User closed the box or clicked on "Cancel"
    });
```
* Display a loading box : `$.loading();`
* Propose a list of choices :
```java
$.choose("Fruit or Yoghourt", new String[]{"Fruit", "Yoghourt", "Both, please"}, new $Utils.ChoiceListener() {
	@Override
	public void onChoose(int id, String choice) {
        // I made my choice
	}

	@Override
	public void onCancel() {
        // I'm not hungry anymore
	}
});
```

### And many other great stuff !
aQuery provides many functions to simplify your life of Android Developer
* Get screen resolution : `$.width()` and `$.height()`
* Open a new Activity : `$.open("ActivityName")`
* Access a value resource : `$.string(R.string.my_string)`, `$.color(R.color.my_color)`, etc
* Convert a unit into another : `$.convert(10, "px","dp")`
* Append views in an XML resource to a parent : `$(R.layout.message, $("#messages_list"));`;

For further details about what you can do, take a look at the [Wiki](https://github.com/tmalahie/aQuery/wiki) and download the [Demo Examples](http://timothe.malahieude.net/projects/aQuery/aQuery-demo.zip).

Want to take part in the project ? Send me an email at t.malahieude@gmail.com to ask to contribute !
