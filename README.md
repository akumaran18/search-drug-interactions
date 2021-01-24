# Drug interactions search feature

## Overview

This is an in-progress AndroidStudio project for a drug interaction app. The app posesses a search feature that uses two API calls to the [National Institute of Health RxNorm dataset](https://www.nlm.nih.gov/research/umls/rxnorm/index.html) to retrieve and describe known interactions and their severity.

## Files

The 'build.gradle (Module app)' file and 'AndroidManifest.xml' files have dependencies that MainActivity.kt cannot function without. The path for MainActivity.kt is app > src > main > java > com > example > myapplication7

The path for activity_main.xml (the user interface) is app > src > main > res> layout

## Sources

The search feature uses the [RxNorm RESTful API](https://rxnav.nlm.nih.gov/RxNormAPIs.html#uLink=RxNorm_REST_getApproximateMatch) to match a search term with an RXCUI in its dataset. The RXCUI is a unique identifier for a single 'concept' (prescription/chemical name) in the dataset. The returned RXCUI is then passed back through a second API call using the [Interaction RESTful API](https://rxnav.nlm.nih.gov/InteractionAPIs.html#uLink=Interaction_REST_findDrugInteractions). This API will return a full list of interactions for the prescription 'concept' matching the given RXCUI.

## Notes

