# Custom NBA Shot Chart Generator
### Shot Data is Up-To-Date Through the End of the 2020-21 Playoffs

Same data, four ways to view it.<br><br>
Heavily inspired by <a href="https://twitter.com/kirkgoldsberry?s=20">Kirk Goldsberry</a> and his work.
<div>
<img src="https://github.com/samnishita/Dynamic-NBA-Shot-Charts/blob/master/src/main/resources/images/SimpleTraditionalExample.png" height="350" width="350">
<img src="https://github.com/samnishita/Dynamic-NBA-Shot-Charts/blob/master/src/main/resources/images/SimpleGridExample.png" height="350" width="350">
<!div>
<div>
<img src="https://github.com/samnishita/Dynamic-NBA-Shot-Charts/blob/master/src/main/resources/images/SimpleZoneExample.png" height="350" width="350">
<img src="https://github.com/samnishita/Dynamic-NBA-Shot-Charts/blob/master/src/main/resources/images/SimpleHeatExample.png" height="350" width="350">
<!div>

### Features
* Over 2,200 past and current NBA players to choose from
* Access to more than 5.3 million shots taken from the 1996-97 season to the 2020-21 season
* Search for shots taken in any Regular Season as well as Preseason and/or Playoffs, when applicable
* Four types of data visualizations: Traditional, Grid, Zone, and Heat
* Advanced Search: filter shots based seasons, players, season types, shot distances, shot success, shot values, shot types, teams 
(home and/or away), and locations on the court


### Details
All shot data is sourced from stats.nba.com via a web scraping algorithm I designed.
<br><h4>*Traditional View*</h4>
Each individual shot is plotted. Makes and misses are indicated by green circles and red X's, respectively. 
Hovering over any shot with the cursor will display the details of that shot.
<br>Limited to 7,500 shots on screen at once.
<br><h4>*Grid View*</h4>
Shots are grouped into uniformly spaced regions across the court. For each region, the color indicates how the FG% of shots in that region
compares to the all-time NBA FG% of shots in that same region, and the size of each square shows the volume of shots taken from that area. 
Higher volume (larger squares) combined with high FG% (more red) indicate very efficient scoring. Uses inverse distance weighting interpolations to produce the map.
<br>Limited to 50,000 shots per search.
<br><h4>*Zone View*</h4>
Shots are grouped shots into various regions of the court. For each region, the total number of made field goals are displayed with the total field goal attempts in each specific region, along with 
that region's calculated FG%. The color of the region shows how its FG% compares to the all-time NBA FG% of that region.
<br>Limited to 50,000 shots per search.
<br><h4>*Heat View*</h4>
Displays the most common shot locations, with more blue indicating high frequency shot locations. Uses inverse distance weighting interpolations to produce the map.
<br>Limited to 50,000 shots per search.

### Installation
Requires Java JDK 8. Visit <a href ="https://www.oracle.com/java/technologies/javase/javase-jdk8-downloads.html">here</a> to download the latest JDK 
(it's free) for your operating system.

Download the latest .jar file (ShotChart1.0.2.jar) from Releases and run it to open the app.

### Future Updates
* Advanced search to include filtering by date, quarter, and time remaining on the game clock
* Display more detailed breakdown of shots (Effective Field Goal Percent, Points Per Shot, Frequency by Location, etc.)
