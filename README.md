## Piklo - Picture Kotlin Loader
Piklo is a small and concise Android Image Loader written in Kotlin. 
This app allows the user to search Flickr photos entering a query (e.g., kittens, dogs, house). To accomplish this, it was necessary to implement three main things:
- Flickr API request;
- Infinite scrolling to display more photos (requesting next page);
- An Image Loader to fetch and display remote images;  

The project was developed using Kotlin, Android Jetpack Components (LiveData, ViewModel) and Kotlin Coroutines (to deal with asynchronous calls & multithreading).
In this project, I tried to apply concepts from clean architecture to build an easy and testable project.

# TODO:
- Implement Tests
- Improve layout
    - Implement Network Status verification


Usage
--------
```kotlin
//Using a ImageView
val url: String = "https://farm1.static.flickr.com/578/23451156376_8983a8ebc7.jpg"
val imageView: ImageView = findViewById(R.id.imageView)
Piklo.get()
    .load(url)
    .into(imageView)

//Using a ImageView with extension (it executes the same Piklo code above)
val imageView: ImageView = findViewById(R.id.imageView)
imageView.load(url)
        
//Getting Bitmap (should be called from a suspend function or coroutine context)
Piklo.get()
    .load(url)
    .get()
```
     
Pitfalls & Known bugs
--------
- Tests are kinda messy and not covering VM and UI classes. To compensate and minimize this issue, I've implemented two UI tests end to end 
(also not the best because network and Flickr API dependency) to test the loading and infinite scrolling. One way to improve this UI tests is by mocking the repository response or view model responses.
- Images not being compressed or resized (consuming extra memory to store it);
- Infinite loading doesn't have a loading indicator;
- Infinite loading implementation sometimes is not loading the next page; (I didn't spend time trying to fix it)

Improvements & next steps
--------
- Increase the test coverage and refactor the tests to follow some standard (which it's kinda messy now)
- Remove android dependencies (e.g., implementing better interfaces) to make the tests less dependent on Android architecture.  
- Implement a `get` bitmap function allowing it being called without coroutines
- Support placeholder and error images
- Implement a function to return the callback completion
- Implement a better structure to deal with errors
- Implement a generic image transformation (e.g., crop, resize, rotate images)
- Improve the image size and compression to store more images in the cache
- Disk cache
- Allow custom view as targets
- Allow load local images, not only from the network (i.e, This could be an external extension library)
- Implement Dependency Injection to facilitate the test implementation by injecting test class dependencies.

Project structure
--------
- *repository* - is where we keep the repository and repository model classes implementations. A repository is where the app can fetch local or remote data.
- *ui* - is the folder where you can find the Activities, Fragments, ViewModels and Adapters.
- *util* - as the name said, is the place where we can add classes and helpers to be used throughout the project. In this folder, you find Constants and Extensions.
    - *imageloader* - you can find Piklo inside this path with its cache and Http structure
        - *`PikloLruCache.kt`* - A PikloImageCache implementation based on Android LruCache implementation to store bitmaps in the memory.   
        - *`PikloHttpClientImpl.kt`* - A PikloHttpClient interface implementation just to do the Http call and fetch the image.
        - *`Piklo.kt`* - The image loader class which processes Http requests and manages the image load. Also, this class keeps track of the ImageView requests, canceling old and unnecessary requests and preventing flickering loading multiple images to the same ImageView (useful for RecyclerViews).     

#### Architecture
---
I tried to apply an MVVM architecture, accessing the repository directly from the View Model. Although I think it's a good practice to implement the UseCase pattern, 
or having a business layer in the middle of the view model and the repository. 
Also, another good practice is by caching the API responses locally and using both remote and local repository (keeping the local repository updated). 

#### Tests
---
I have implemented some tests for the image loader, cache, api, repository. After all, I did not implement as many tests as I would like to.
As mentioned before, most of the tests are highly dependent on Roboletric because I didn't implement some interfaces to reduce the dependency with some Android objects.

To execute the UI tests, you should start an android emulator with internet connection and run the command below inside the root project folder. :) 
```bash
./gradlew connectedCheck
```

To execute the unit tests, you should run the command below:
```bash
./gradlew test
```

- Libraries
Some of the libraries that were used in this project aim to facilitate and speed-up the implementation of the test. 
- Robolectric
- Mockito


Compatibility
--------
*minSdkVersion* 19 (Kitkat 4.4)  
*targetSdkVersion* 29 (Android 10)


#### Time spent
---
- Creating Piklo: ~4 hours.
- Creating Project architecture: 1 hour.
- Creating and testing HTTP & Flickr API requests: ~1-2 hours.
- Refactoring & Implementing tests: ~5 hours.
- Writing README: 30min