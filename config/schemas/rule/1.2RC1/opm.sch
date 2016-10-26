<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2">
   <sch:title>Schematron validation</sch:title>
   <sch:ns prefix="opm" uri="http://def.wmo.int/opm/2013"></sch:ns>
   <sch:ns prefix="sam" uri="http://www.opengis.net/sampling/2.0"></sch:ns>
   <sch:ns prefix="sams" uri="http://www.opengis.net/samplingSpatial/2.0"></sch:ns>
   <sch:ns prefix="xlink" uri="http://www.w3.org/1999/xlink"></sch:ns>
   <sch:ns prefix="om" uri="http://www.opengis.net/om/2.0"></sch:ns>
   <sch:ns prefix="gml" uri="http://www.opengis.net/gml/3.2"></sch:ns>
   <sch:pattern id="OPM.COP1">
      <sch:rule context="//opm:CompositeObservableProperty">
         <sch:assert test="(string(@count) eq string(count(opm:property)))">OPM.COP1: Attribute 'count' shall specify the number of observed
            physical properties (specified via the 'property' association role) aggregated within
            this composite observable property.
         </sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern id="OPM.QOP1">
      <sch:rule context="//opm:QualifiedObservableProperty">
         <sch:assert test="((: TO BE IMPLEMENTATED WHEN REPOSITORY IS READY :) true())">OPM.QOP1: unitOfMeasure shall be appropriate for baseProperty</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern id="OPM.RB1">
      <sch:rule context="//opm:RangeBounds">
         <sch:assert test="(number( opm:rangeStart/text() ) lt number( opm:rangeEnd/text() ))">OPM.RB1: The extreme lower limit of the range of interval must be less than the
            extreme upper limit.
         </sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern id="OPM.RC1">
      <sch:rule context="//opm:RangeConstraint">
         <sch:assert test="((: TO BE IMPLEMENTATED WHEN REPOSITORY IS READY :) true())">OPM.RC1: unitOfMeasure shall be appropriate for constraintProperty</sch:assert>
      </sch:rule>
   </sch:pattern>
   <sch:pattern id="OPM.SC1">
      <sch:rule context="//opm:ScalarConstraint">
         <sch:assert test="((: TO BE IMPLEMENTATED WHEN REPOSITORY IS READY :) true())">OPM.SC1: unitOfMeasure shall be appropriate for constraintProperty</sch:assert>
      </sch:rule>
   </sch:pattern>
</sch:schema>
