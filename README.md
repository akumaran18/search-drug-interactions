# Drug Interaction Search Feature

## Overview

<img align="right" src="https://media.giphy.com/media/fiZf1kMUFPDOMuS5O9/giphy.gif">

This is an in-progress Android Studio project for a drug interaction app. The app posesses a search feature that uses two API calls to the [National Institute of Health RxNorm dataset](https://www.nlm.nih.gov/research/umls/rxnorm/index.html) to retrieve and describe known interactions and their severity.

The search feature uses the [RxNorm RESTful API](https://rxnav.nlm.nih.gov/RxNormAPIs.html#uLink=RxNorm_REST_getApproximateMatch) to match a search term with an RXCUI in the dataset. The RXCUI is a unique identifier for a single 'concept' (prescription/chemical name) in the dataset. The returned RXCUI is then passed back through a second API call using the [Interaction RESTful API](https://rxnav.nlm.nih.gov/InteractionAPIs.html#uLink=Interaction_REST_findDrugInteractions). This API will return a full list of interactions for the prescription 'concept' matching the given RXCUI. The RxNorm RESTful API returns XML which must be parsed, and the Interaction RESTful API returns JSON which must be parsed.

The search results display 'Comments' returned from the RxNorm RESTful API, the number of interactions found, and the full list of interactions.

## Files

The 'build.gradle (Module app)' file and 'AndroidManifest.xml' files have dependencies that MainActivity.kt cannot function without. The path for MainActivity.kt is app > src > main > java > com > example > myapplication7

The path for activity_main.xml (the user interface) is app > src > main > res> layout

The API calls are enabled by the HTTP library for Android, [Volley](https://github.com/google/volley).

## Notes

This project is still in-progress. Search validation has discrepancies: searching words that are not brand/prescription drug names will sometimes retrieve an RXCUI and sometimes not.
