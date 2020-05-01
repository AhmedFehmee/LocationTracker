# LocationTracker
Simply you can get the location with three options 
- Using GPS
- Using Network and get the last known location
- Using Sim card 

## Note 
#### Sim Card 
App will get the phone tower details with it's location (This is not the real location, it's the location of the nearest Phone tower)
will need Phone status permissions 
```` 
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
````
with using Telephony Manager app will get mcc, mnc, lac and cid 
You can access public databases to convert these values to lat/lon.
Databases include:
- [Unwired Labs API](https://unwiredlabs.com/)
- [OpenCellID](https://opencellid.org/)

Cell ID is the ID of the cell phone tower your phone/device is connected to. The moment you move a bit, or the signal of another tower nearby is better than the current one, your phone will switch over to that tower, and your Cell ID now reflects the ID of that tower.

#### GPS 
App will need Location Permissions 
````
     <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
````
and all work will be exist in LocationTracker Class

### [OpenStreetMap](https://www.openstreetmap.org/) 
it's a free to use under an open license and it's easy to use 
app using it instead of Google maps 

## Future Enhancements
Adding user story with using firebase 
