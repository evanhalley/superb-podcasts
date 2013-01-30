superb-podcasts
===============
Superb Podcast development environment setup

Software
Eclipse IDE*
Android SDK*
*Can be downloaded as a bundle from http://developer.android.com/sdk/index.html as a bundle
GIT Client (Tortoise SVN on Windows, SourceTree on Mac OS X,  whatever you can get on Linux)

High Level Setup Process
1) Download install/unzip Eclipse IDE
2) Download Android SDK Tootls for Eclipse
3) Download install/unzip Android SDK
   Steps 1-3 become one step if downloading the bundle from developer.android.com
4) Everything will reside in a folder, once the next steps are complete, that folder should look like:

superb-podcasts/
|--SlidingMenu/
|--Superb/
|--Superb-test/

a) Checkout the Superb podcasts source code, this will create a directory called superb-podcasts and check the source and test project
   out to two child folders
   git clone git clone git@git.assembla.com:superb-podcasts.git
b) change your directory to superb-podcasts and checkout the SlidingMenu source code
   git clone git://github.com/jfeinstein10/SlidingMenu.git
   
5) Open Eclipse and Import Existing projects into workspace
   a) navigate to superb-podcasts/SlidingMenu/library and import SlidingMenu
   b) navigate to superb-podcasts/Superb and import Superb
6) At this point, Eclipse should be ready to build the project
