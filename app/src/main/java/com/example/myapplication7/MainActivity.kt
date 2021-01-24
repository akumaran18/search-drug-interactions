package com.example.myapplication7

import android.app.Activity
import android.app.VoiceInteractor
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.renderscript.RenderScript.Priority
import android.util.Log
import android.util.LruCache
import android.util.Xml
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.ImageLoader
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject

import org.w3c.dom.Text
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.IOException
import java.io.InputStream
import java.net.URL
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //val textView = findViewById<TextView>(R.id.text)

// ...


        //STEP ONE: Search a term, return array of possible RXCUIs
        //Input: Search string
        //Output: Array of RXCUIs ranked from best to lowest match


        //Statement for Class Regex so that you can use regex functions
        class Regex

        //New var to hold our search string, e.g. "zicor 10 mg" or "zoloft"
        var searchTerm  = ""

        //New var foundRxcui is an empty arrayList of type String
        var foundRxcui = arrayListOf<String>()

        //New var for comment is an empty arrayList of type String
        var comment = arrayListOf<String>()

        //Function takes in searched medication, puts out list of RXCUI from best to worst match
        suspend fun getRxcuiMatches(search: String): ArrayList<String> {
            //textView1.text = empty
            //textView1.text = ""
            //Trim the leading and trailing white space, then replace all internal white space with %20
            searchTerm = search.trim().replace("[ ]+".toRegex(), "%20")
            //Request a string response from the provided URL
            val url1 =
                "https://rxnav.nlm.nih.gov/REST/approximateTerm?term=" + searchTerm + "&maxEntries=1"

            /////TEST//////////////////////////////////////////////

            suspend fun makeContact1(url: String) = suspendCoroutine<String?> { cont ->

                val stringRequest = StringRequest(Request.Method.GET, url,
                    Response.Listener<String> { response ->
                        cont.resume(response)
                    },
                    Response.ErrorListener { cont.resume(null) })

                //Add request to the request queue
                MySingleton.getInstance(this).addToRequestQueue(stringRequest)
            }
            ////////////////////////////////////////////////////////
            // Store the response in var response1
            var response1 = makeContact1(url1).toString()

            //pattern1 picks out all strings with one ore more chars that come right between the comment tags
            val pattern1 = Regex(">(.*?)</comment>")
            comment = arrayListOf<String>()
            //All matches to pattern1 are stored in sequence called ans1
            val ans1: Sequence<MatchResult> = pattern1.findAll(response1, 0)
            //forEach loop keeps substring before last "</" and applies substring after last > for each found match and stores in empty array comment
            ans1.forEach() { matchResult ->
                comment.add(matchResult.value.substringBeforeLast(delimiter = "</", missingDelimiterValue = "Last End Tag </ Not Found").substringAfterLast(delimiter = ">", missingDelimiterValue = "Last Right Caret > Not Found").toString())
            }

            if(comment.toString() == "[No drugs identified; ]"){
                return comment
            }
            else{
                //pattern2 picks out all strings with one ore more numbers that come directly before </rxcui> tag
                val pattern2 = Regex("[0-9]+</rxcui>")
                //All matches to pattern2 are stored in sequence called ans1
                val ans2: Sequence<MatchResult> = pattern2.findAll(response1, 0)

                //forEach loop keeps substring before "</" for each found match and stores in empty array foundRxcui
                ans2.forEach() { matchResult ->
                    foundRxcui.add(matchResult.value.substringBeforeLast(delimiter = "</", missingDelimiterValue = "Last End Tag </ Not Found").toString())
                }

                //Restore original user search term in the variable searchTerm
                searchTerm = search

                //Return foundRxcui at the end of the function
                return foundRxcui
                //return comment
            }
        }

        /*lifecycleScope.launch {
            val result =  getRxcuiMatches("zocor 10 mg")
            textView1.setText(result.toString())
            //textView2.setText(result.toString())
        }*/

        //STEP TWO: Search each item in RXCUI array, return the drug names
        //Input: Each RXCUI
        //Output: Array of drug names ranked from best to lowest match

        //Instantiate the RequestQueue.
        //val queue = Volley.newRequestQueue(this)

        //This function takes in the rxcui and returns the real drug name and
        //could be modified to also return drug interactions relevant to users' medication list

        suspend fun nameFromRxcui(foundRxcui: ArrayList<String>): String {
            //Take int rxcui, convert to string
            //var rxcui = rxcui.toString()
            //URL for API call for specific rxcui
            //val url2 = "https://rxnav.nlm.nih.gov/REST/interaction/interaction.json?rxcui=" + rxcui + "&sources=ONCHigh"

            //JSONObject error message for jsonObjectRequest
            val error1 = JSONObject("""{"message":"request encountered error"}""")

            //Request a JSON Object response from the provided URL
            suspend fun makeContact2(url: String) = suspendCoroutine<JSONObject> { cont ->

                val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url, null,
                    Response.Listener { response ->
                        cont.resume(response)
                    },
                    Response.ErrorListener { cont.resume(error1) })

                //Add request to the request queue
                MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)
            }

            //Takes in ANY JSON Object minConceptItem ('something'), converts it to array list of strings
            fun makeArray(something: JSONObject): ArrayList<String> {
                //Convert JSON Object to string var myString
                var myString = something.toString()
                //Remove the bracket at the start and end
                myString = myString.dropLast(1).drop(1)
                //Store myString in second var myString2
                var myString2 = myString.toString()
                //Declare empty array list of type string called array2
                var array2= arrayListOf<String>()
                //Declare empty variable beforeFirstComma
                var beforeFirstComma = ""
                //While beforeFirstComma is not equal to "First Comma Not Found",
                while (beforeFirstComma != "First Comma Not Found") {
                    //If first comma is found in myString2, find substring before first comma and store in beforeFirstComma
                    beforeFirstComma = myString2.substringBefore(delimiter = ",", missingDelimiterValue = "First Comma Not Found")
                    //If first comma IS successfully found,
                    if (beforeFirstComma != "First Comma Not Found") {
                        //Append beforeFirstComma to empty array array2
                        array2.add(beforeFirstComma)
                        //Then store substring AFTER first comma in myString2
                        myString2 = myString2.substringAfter(delimiter = ",", missingDelimiterValue = "First Comma Not Found")
                    }
                    //If first comma is NOT FOUND (meaning beforeFirstComma IS equal to "First Comma Not Found"),
                    else {
                        //Append leftover string in myString2 to array2
                        array2.add(myString2)
                    }
                }
                //Return array2 as an ArrayList<String>, an array version of minConceptItem
                return array2
            }

            //Set up empty string name
            var name = ""
            //Set up empty ArrayList<String> nameArray
            var nameArray = arrayListOf<String>()

            //////////////////////////////////////////////////////////////////////
            /*var resp = makeContact2("https://rxnav.nlm.nih.gov/REST/interaction/interaction.json?rxcui=104490&sources=ONCHigh")
            var interactionPair = resp.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0).getJSONArray("interactionPair")
            var indexedObject = JSONObject()
            var i = 0
            //while (i < interactionPair.length()) {
            indexedObject = interactionPair.getJSONObject(i)
            var minConIt1 = indexedObject.getJSONArray("interactionConcept").getJSONObject(1).getJSONObject("minConceptItem")
            var interactionName = makeArray(minConIt1).elementAt(1).toString().substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1).capitalize()
            var severity1 = indexedObject.toString().dropLast(1).drop(1).substringBeforeLast(delimiter = ",", missingDelimiterValue = "Last Comma Not Found").substringAfterLast(delimiter = ",", missingDelimiterValue = "Last Comma Not Found").substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1).capitalize()
            var description1 = indexedObject.toString().dropLast(1).drop(1).substringAfterLast(delimiter = ",", missingDelimiterValue = "Last Comma Not Found").substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1)
            var answer = "Interaction with: " + interactionName + "\nRisk Severity: " + severity1 + "\nDescription: " + description1*/
            ////////////////////////////////////////////////////////////////////////

            //We're just going to grab the first returned RXCUI because the second one is redundant
            var foundRxcui = foundRxcui[0]
            //Set up our empty string variable for a single drug interaction
            var oneInteraction = ""
            //Set up our final returned answer as an arrayListOf<String>()
            var answerArray = arrayListOf<String>()

            //Remember foundRxcui is an ArrayList<String> that looks like [104490, 563653]
            //foundRxcui.forEach() { num -> supposed to use num to represent a single indexed object in array and perform operations on it
            //URL for API call for specific rxcui
            val url2 = "https://rxnav.nlm.nih.gov/REST/interaction/interaction.json?rxcui=" + foundRxcui + "&sources=ONCHigh"
            //Make the API call using url2 and store response in var response2
            var response2 = makeContact2(url2)
            //What getJSONArray does is it takes a JSONObject and returns a JSONArray,
            //you cannot string ".getJSONArray"s together without converting returned arrays to objects
            //The interaction pair has 5 interaction concepts
            var interactionPair = response2.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0).getJSONArray("interactionPair")

            ////////////////////////////////////////////////////////////////////////////
            //Create empty JSONObject() called indexedObject (the interactionPair has one JSONObject for each drug interaction, we will index through them)
            var indexedObject = JSONObject()
            var i = 0
            

            while (i < interactionPair.length()) {

                indexedObject = interactionPair.getJSONObject(i)
                var minConIt1 = indexedObject.getJSONArray("interactionConcept").getJSONObject(1).getJSONObject("minConceptItem")
                var interactionName = makeArray(minConIt1).elementAt(1).toString().substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1).capitalize()

                var severity1 = indexedObject.toString().dropLast(1).drop(1).substringBeforeLast(delimiter = ",", missingDelimiterValue = "Last Comma Not Found").substringAfterLast(delimiter = ",", missingDelimiterValue = "Last Comma Not Found").substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1).capitalize()
                var description1 = indexedObject.toString().dropLast(1).drop(1).substringAfterLast(delimiter = ",", missingDelimiterValue = "Last Comma Not Found").substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1)

                oneInteraction = "\n" + "Interacts with: " + interactionName + "\nRisk Severity: " + severity1 + "\nDescription: " + description1 + "\n"
                answerArray.add(oneInteraction)

                i = i + 1
            }

            ////////////////////////////////////////////////////////////////////////////////

            //This is the minConceptItem containing the Name
            var minConItName = interactionPair.getJSONObject(0).getJSONArray("interactionConcept").getJSONObject(0).getJSONObject("minConceptItem")
            //var rxcui1 = makeArray(minConIt1).elementAt(0).toString().substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1)
            //Picks out drug's name name from array made out of first minConceptItem
            name = makeArray(minConItName).elementAt(1).toString().substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1).capitalize()
            nameArray.add(name)
            //}

            //Return nameArray as a string
            //return nameArray.toString()
            var editedComment = ""
            if(comment.toString() == "[]"){
                editedComment = "None"
            }
            else{
                editedComment = comment.toString().dropLast(3).drop(1)
            }
            var viewResults = ("\n" +"Searched '" + searchTerm + "'" + "\n" + "Comments: " + editedComment + "\n\n" + "Results:" + "\n\n" + "Active ingredient: " + name + ", " + interactionPair.length() + " interactions found" + "\n" + answerArray.toString().dropLast(1).drop(1)).toString()

            return viewResults
        }



        button1.setOnClickListener {
            //Set textView2 as empty
            textView2.setText("")
            //Whenever clicked, must check string in editSearch
            searchTerm = editSearch.text.toString()

            if (searchTerm == "" || searchTerm == "Type here...") {
                textView2.setText("You haven't searched anything yet")
            }
            else {
                lifecycleScope.launch {
                    var result1 = getRxcuiMatches(searchTerm)


                    if (comment.toString() == "[No drugs identified; ]") {
                        var noDrugsFound = ("\n" + "Searched '" + searchTerm + "'" + "\n" + "Comments: " + comment.toString().dropLast(3).drop(1))
                        textView2.setText(noDrugsFound)
                    }
                    else {
                        var result2 = nameFromRxcui(result1)
                        textView2.setText(result2)
                    }
                }
            }
        }




        //For each interactionConcept in interactionPair, pick index 1 minConceptItem and index 1 of array2
        //To see if drug name in interaction matches one of the saved drugs in the user's list
        //If drug name matches, get severity and description of that interactionConcept and display it

        //STEP THREE: Get name, severity, and description of drugs that the original drug interacts with
        //Input:
        //Output:


/*      val url2 = "https://rxnav.nlm.nih.gov/REST/interaction/interaction.json?rxcui=88014&sources=ONCHigh"

        val jsonObjectRequest = JsonObjectRequest(Request.Method.GET, url2, null,
            Response.Listener { response ->
                val response2 = response
                //textView1.text = "Response: %s".format(response1.toString())

                //What getJSONArray does is it takes a JSONObject and returns a JSONArray,
                //you cannot string ".getJSONArray"s together without converting returned arrays to objects

                //The interaction pair has 5 interaction concepts
                var interactionPair = response2.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0).getJSONArray("interactionPair")

                //This is the last of the 5 interaction concepts
                var concept1 = interactionPair.getJSONObject(4)
                //response1.getJSONArray("interactionTypeGroup").getJSONObject(0).getJSONArray("interactionType").getJSONObject(0).getJSONArray("interactionPair").getJSONObject(4)

                //This ithe second minConceptItem of the two in the last interactionConcept
                var minConIt1 = concept1.getJSONArray("interactionConcept").getJSONObject(1).getJSONObject("minConceptItem")

                fun pickMinConcept(concept: Int, minConIt: Int):JSONObject {
                    return interactionPair.getJSONObject(concept).getJSONArray("interactionConcept").getJSONObject(minConIt).getJSONObject("minConceptItem")
                }

                //Takes in supplied variable that is labeled 'something' within the function
                //that is of type JSONObject and returns a value of type String
                fun makeArray(something: JSONObject): ArrayList<String> {
                    var myString = something.toString()

                    //Remove the bracket at the start and end
                    myString = myString.dropLast(1).drop(1)

                    //Convert the returned JSON Object myString to a string, store in second var myString2
                    var myString2 = myString.toString()

                    //Declare empty array list of type string called array2
                    var array2= arrayListOf<String>()

                    //Declare empty variable beforeFirstComma
                    var beforeFirstComma = ""

                    //While beforeFirstComma is not equal to "First Comma Not Found",
                    while (beforeFirstComma != "First Comma Not Found") {
                        //If comma is found in myString2, find substring before first comma and store in beforeFirstComma
                        beforeFirstComma = myString2.substringBefore(delimiter = ",", missingDelimiterValue = "First Comma Not Found")
                        //If first comma is successfully found
                        if (beforeFirstComma != "First Comma Not Found") {
                            //Append beforeFirstComma to empty array array2
                            array2.add(beforeFirstComma)
                            //Store substring AFTER first comma in myString2
                            myString2 = myString2.substringAfter(delimiter = ",", missingDelimiterValue = "First Comma Not Found")
                        }
                        //If first comma is NOT FOUND (meaning beforeFirstComma IS equal to "First Comma Not Found")
                        else {
                            //Append leftover string in myString2 to array2
                            array2.add(myString2)
                        }
                    }
                    //Return array2 as an ArrayList<String> type
                    return array2
                }
                var rxcui1 = makeArray(minConIt1).elementAt(0).toString().substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1)
                var name1 = makeArray(minConIt1).elementAt(1).toString().substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1)

                //LOOK AT THE SUBSTRING AFTER THE VERY LAST COMMA OPERATION TO RETURN SEVERITY AND DESCRIPTION! You can do it
                //var risk1 = makeArray(concept1).elementAt(3).toString()
                var description1 = concept1.toString().dropLast(1).drop(1).substringAfterLast(delimiter = ",", missingDelimiterValue = "Last Comma Not Found").substringAfter(delimiter = ":", missingDelimiterValue = "First Colon Not Found").dropLast(1).drop(1)
                //var severity1:
                //.getJSONArray("interactionConcept")
                    //.getJSONObject(1).getJSONObject("minConceptItem")

                textView2.text = (
                 "Last concept: " + concept1.toString()
                )
                //"# of interaction pairs:" + interactionPair.length().toString()
            },
            Response.ErrorListener { error ->
                // TODO: Handle error
                textView2.text = "That didn't work!"
            }
        )*/

// Access the RequestQueue through your singleton class.

        //MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest)

/*// Request a string response from the provided URL.
        val stringRequest = StringRequest(Request.Method.GET, url,
            Response.Listener<String> { response ->
                // Display the first 500 characters of the response string.
                textView1.text = "Response is: ${response.substring(0, 500)}"
            },
            Response.ErrorListener { textView1.text = "That didn't work!" })

// Add the request to the RequestQueue.
        queue.add(stringRequest)*/

    }
}

class MySingleton constructor(context: Context) {
    companion object {
        @Volatile
        private var INSTANCE: MySingleton? = null
        fun getInstance(context: Context) =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: MySingleton(context).also {
                    INSTANCE = it
                }
            }
    }
    val imageLoader: ImageLoader by lazy {
        ImageLoader(requestQueue,
            object : ImageLoader.ImageCache {
                private val cache = LruCache<String, Bitmap>(20)
                override fun getBitmap(url: String): Bitmap {
                    return cache.get(url)
                }
                override fun putBitmap(url: String, bitmap: Bitmap) {
                    cache.put(url, bitmap)
                }
            })
    }
    val requestQueue: RequestQueue by lazy {
        // applicationContext is key, it keeps you from leaking the
        // Activity or BroadcastReceiver if someone passes one in.
        Volley.newRequestQueue(context.applicationContext)
    }
    fun <T> addToRequestQueue(req: Request<T>) {
        requestQueue.add(req)
    }
}
