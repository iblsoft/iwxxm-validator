Workaround for problem with original iwxxm-collect.sch
-------------------------------------------------------

sch:include elements are used in the original iwxxm-collect.sch file. Original
files includes iwxxm.sch and collect.sch from iwxxm 1.2. Using of sch:include is 
not supported by schematron validation rules. Our validator runs schematron validation 
against every .sch file placed in the rule directory (for appropriate version 
of iwxxm). Workaround for problem with includes is to rename (or remove) original 
iwxxm-collect.sch file to iwxxm-collect.sch.txt and copy collect.sch from 1.2 rules 
also into 2.0 rule directory.
