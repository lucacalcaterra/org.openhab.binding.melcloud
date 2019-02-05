package org.openhab.binding.melcloud.json;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ServerDatasObject {

    @SerializedName("ID")
    @Expose
    private Integer iD;
    @SerializedName("Name")
    @Expose
    private String name;
    @SerializedName("AddressLine1")
    @Expose
    private String addressLine1;
    @SerializedName("AddressLine2")
    @Expose
    private Object addressLine2;
    @SerializedName("City")
    @Expose
    private String city;
    @SerializedName("Postcode")
    @Expose
    private String postcode;
    @SerializedName("Latitude")
    @Expose
    private Double latitude;
    @SerializedName("Longitude")
    @Expose
    private Double longitude;
    @SerializedName("District")
    @Expose
    private Object district;
    @SerializedName("FPDefined")
    @Expose
    private Boolean fPDefined;
    @SerializedName("FPEnabled")
    @Expose
    private Boolean fPEnabled;
    @SerializedName("FPMinTemperature")
    @Expose
    private Integer fPMinTemperature;
    @SerializedName("FPMaxTemperature")
    @Expose
    private Integer fPMaxTemperature;
    @SerializedName("HMDefined")
    @Expose
    private Boolean hMDefined;
    @SerializedName("HMEnabled")
    @Expose
    private Boolean hMEnabled;
    @SerializedName("HMStartDate")
    @Expose
    private Object hMStartDate;
    @SerializedName("HMEndDate")
    @Expose
    private Object hMEndDate;
    @SerializedName("BuildingType")
    @Expose
    private Integer buildingType;
    @SerializedName("PropertyType")
    @Expose
    private Integer propertyType;
    @SerializedName("DateBuilt")
    @Expose
    private String dateBuilt;
    @SerializedName("HasGasSupply")
    @Expose
    private Boolean hasGasSupply;
    @SerializedName("LocationLookupDate")
    @Expose
    private String locationLookupDate;
    @SerializedName("Country")
    @Expose
    private Integer country;
    @SerializedName("TimeZoneContinent")
    @Expose
    private Integer timeZoneContinent;
    @SerializedName("TimeZoneCity")
    @Expose
    private Integer timeZoneCity;
    @SerializedName("TimeZone")
    @Expose
    private Integer timeZone;
    @SerializedName("Location")
    @Expose
    private Integer location;
    @SerializedName("CoolingDisabled")
    @Expose
    private Boolean coolingDisabled;
    @SerializedName("Expanded")
    @Expose
    private Boolean expanded;
    @SerializedName("Structure")
    @Expose
    private Structure structure;
    @SerializedName("AccessLevel")
    @Expose
    private Integer accessLevel;
    @SerializedName("DirectAccess")
    @Expose
    private Boolean directAccess;
    @SerializedName("MinTemperature")
    @Expose
    private Integer minTemperature;
    @SerializedName("MaxTemperature")
    @Expose
    private Integer maxTemperature;
    @SerializedName("Owner")
    @Expose
    private Object owner;
    @SerializedName("EndDate")
    @Expose
    private String endDate;
    @SerializedName("iDateBuilt")
    @Expose
    private Object iDateBuilt;
    @SerializedName("QuantizedCoordinates")
    @Expose
    private QuantizedCoordinates quantizedCoordinates;

    public Integer getID() {
        return iD;
    }

    public void setID(Integer iD) {
        this.iD = iD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public Object getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(Object addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Object getDistrict() {
        return district;
    }

    public void setDistrict(Object district) {
        this.district = district;
    }

    public Boolean getFPDefined() {
        return fPDefined;
    }

    public void setFPDefined(Boolean fPDefined) {
        this.fPDefined = fPDefined;
    }

    public Boolean getFPEnabled() {
        return fPEnabled;
    }

    public void setFPEnabled(Boolean fPEnabled) {
        this.fPEnabled = fPEnabled;
    }

    public Integer getFPMinTemperature() {
        return fPMinTemperature;
    }

    public void setFPMinTemperature(Integer fPMinTemperature) {
        this.fPMinTemperature = fPMinTemperature;
    }

    public Integer getFPMaxTemperature() {
        return fPMaxTemperature;
    }

    public void setFPMaxTemperature(Integer fPMaxTemperature) {
        this.fPMaxTemperature = fPMaxTemperature;
    }

    public Boolean getHMDefined() {
        return hMDefined;
    }

    public void setHMDefined(Boolean hMDefined) {
        this.hMDefined = hMDefined;
    }

    public Boolean getHMEnabled() {
        return hMEnabled;
    }

    public void setHMEnabled(Boolean hMEnabled) {
        this.hMEnabled = hMEnabled;
    }

    public Object getHMStartDate() {
        return hMStartDate;
    }

    public void setHMStartDate(Object hMStartDate) {
        this.hMStartDate = hMStartDate;
    }

    public Object getHMEndDate() {
        return hMEndDate;
    }

    public void setHMEndDate(Object hMEndDate) {
        this.hMEndDate = hMEndDate;
    }

    public Integer getBuildingType() {
        return buildingType;
    }

    public void setBuildingType(Integer buildingType) {
        this.buildingType = buildingType;
    }

    public Integer getPropertyType() {
        return propertyType;
    }

    public void setPropertyType(Integer propertyType) {
        this.propertyType = propertyType;
    }

    public String getDateBuilt() {
        return dateBuilt;
    }

    public void setDateBuilt(String dateBuilt) {
        this.dateBuilt = dateBuilt;
    }

    public Boolean getHasGasSupply() {
        return hasGasSupply;
    }

    public void setHasGasSupply(Boolean hasGasSupply) {
        this.hasGasSupply = hasGasSupply;
    }

    public String getLocationLookupDate() {
        return locationLookupDate;
    }

    public void setLocationLookupDate(String locationLookupDate) {
        this.locationLookupDate = locationLookupDate;
    }

    public Integer getCountry() {
        return country;
    }

    public void setCountry(Integer country) {
        this.country = country;
    }

    public Integer getTimeZoneContinent() {
        return timeZoneContinent;
    }

    public void setTimeZoneContinent(Integer timeZoneContinent) {
        this.timeZoneContinent = timeZoneContinent;
    }

    public Integer getTimeZoneCity() {
        return timeZoneCity;
    }

    public void setTimeZoneCity(Integer timeZoneCity) {
        this.timeZoneCity = timeZoneCity;
    }

    public Integer getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(Integer timeZone) {
        this.timeZone = timeZone;
    }

    public Integer getLocation() {
        return location;
    }

    public void setLocation(Integer location) {
        this.location = location;
    }

    public Boolean getCoolingDisabled() {
        return coolingDisabled;
    }

    public void setCoolingDisabled(Boolean coolingDisabled) {
        this.coolingDisabled = coolingDisabled;
    }

    public Boolean getExpanded() {
        return expanded;
    }

    public void setExpanded(Boolean expanded) {
        this.expanded = expanded;
    }

    public Structure getStructure() {
        return structure;
    }

    public void setStructure(Structure structure) {
        this.structure = structure;
    }

    public Integer getAccessLevel() {
        return accessLevel;
    }

    public void setAccessLevel(Integer accessLevel) {
        this.accessLevel = accessLevel;
    }

    public Boolean getDirectAccess() {
        return directAccess;
    }

    public void setDirectAccess(Boolean directAccess) {
        this.directAccess = directAccess;
    }

    public Integer getMinTemperature() {
        return minTemperature;
    }

    public void setMinTemperature(Integer minTemperature) {
        this.minTemperature = minTemperature;
    }

    public Integer getMaxTemperature() {
        return maxTemperature;
    }

    public void setMaxTemperature(Integer maxTemperature) {
        this.maxTemperature = maxTemperature;
    }

    public Object getOwner() {
        return owner;
    }

    public void setOwner(Object owner) {
        this.owner = owner;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public Object getIDateBuilt() {
        return iDateBuilt;
    }

    public void setIDateBuilt(Object iDateBuilt) {
        this.iDateBuilt = iDateBuilt;
    }

    public QuantizedCoordinates getQuantizedCoordinates() {
        return quantizedCoordinates;
    }

    public void setQuantizedCoordinates(QuantizedCoordinates quantizedCoordinates) {
        this.quantizedCoordinates = quantizedCoordinates;
    }

}