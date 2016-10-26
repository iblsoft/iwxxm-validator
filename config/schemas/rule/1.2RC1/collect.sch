<sch:schema xmlns:sch="http://purl.oclc.org/dsdl/schematron" queryBinding="xslt2">
    <sch:title>Schematron validation</sch:title>
    <sch:ns prefix="collect" uri="http://def.wmo.int/collect/2014"/>
    <sch:pattern id="COLLECT.MB1">
        <sch:rule context="//collect:MeteorologicalBulletin">
            <sch:assert test="(count(distinct-values(for $item in //collect:meteorologicalInformation/child::node() return(node-name($item))))eq 1)">COLLECT.MB1: Feature collection instances in MeteorologicalBulletin should be of the same type</sch:assert>
        </sch:rule>
    </sch:pattern>
</sch:schema>
