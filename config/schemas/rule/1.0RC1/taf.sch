<?xml version="1.0" encoding="UTF-8"?>
<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" queryBinding="xslt2">
    <!-- 
    Ensure TAF Observation information is of the correct type.  
        
    The current method for testing the type of the SF_SpatialSamplingFeature is quite strict: this type is an xlink 
    and therefore impossible to validate except by mechanistically testing the xlink:href result
        
    Similarly, due to the xlink it is difficult to ensure that the featureOfInterest/sampledFeature type is correct.
    The xlink:href could be checked to ensure it starts with the URI prefix "http://icao.int/id/aerodrome/", but this ended
    up being a problem in the case of local xlink references and was deemed to brittle and as such the rules are commented
    out.
        
    The current rules do not allow for extension of elements (they work ONLY on IWXXM elements ignoring substitution groups).  
    This should be converted to use the schema-element[iwxxm:MeteorologicalAerodromeObservationRecord] terminology
    to allow for substitution groups and other extensions to these types.  The current restriction is in place because a
    free (and therefore heavily used) Schematron parser has not yet been found that can validate content with 'schema-element'
    due to its reliance on xsl:import-schema
    -->
    <sch:ns prefix="om" uri="http://www.opengis.net/om/2.0"/>
    <sch:ns prefix="sams" uri="http://www.opengis.net/samplingSpatial/2.0"/>
    <sch:ns prefix="sf" uri="http://www.opengis.net/sampling/2.0"/>
    <sch:ns prefix="iwxxm" uri="http://icao.int/iwxxm/1.0RC1" />
    <sch:ns prefix="xlink" uri="http://www.w3.org/1999/xlink" />
    
    <sch:pattern>

        <sch:rule context="//iwxxm:TAF">
            <sch:assert test="iwxxm:baseForecast/om:OM_Observation/om:result/iwxxm:MeteorologicalAerodromeForecastRecord">
                Result should be a MeteorologicalAerodromeForecastRecord
            </sch:assert>
            <sch:assert test="iwxxm:baseForecast/om:OM_Observation/om:featureOfInterest/sams:SF_SpatialSamplingFeature/sf:type[@xlink:href='http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint']">
                featureOfInterest should be an SF_SamplingPoint (type is an xlink:href to http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint)
            </sch:assert>
        </sch:rule>
        
        <sch:rule context="//iwxxm:TAF">
            <sch:assert test="iwxxm:changeForecast/om:OM_Observation/om:result/iwxxm:MeteorologicalAerodromeForecastRecord">
                Result should be a MeteorologicalAerodromeForecastRecord
            </sch:assert>
            <sch:assert test="iwxxm:changeForecast/om:OM_Observation/om:featureOfInterest/sams:SF_SpatialSamplingFeature/sf:type[@xlink:href='http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint']">
                featureOfInterest should be an SF_SamplingPoint (type is an xlink:href to http://www.opengis.net/def/samplingFeatureType/OGC-OM/2.0/SF_SamplingPoint)
            </sch:assert>
        </sch:rule>
        
    </sch:pattern>
</sch:schema>