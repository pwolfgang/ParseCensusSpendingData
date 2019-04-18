# Program to parse and load Census Spending Data



This program parses the Public Use File from the U. S. Census.

For the years 2000 through 2015 the data files are available at 
[http://www2.census.gov/govs/state/xxstate35.txt] where xx is the year.  
Format of this data is described in
[https://www2.census.gov/programs-surveys/state/technical-documentation/file-layouts/public-use-file-layout.csv]
These files contained only national and state level data.

For 2016 and presumably beyond data is available at:
[https://www.census.gov/programs-surveys/gov-finances/data/datasets.html]
The data is available in a zip file of the form: xxxx_Individual_Unit_file.zip.
The data itself is in the file xxxxFinEstDAT_yyyyyyyymodp_pu.txt. The format
of the file is described in xxxx S&L Indiv Unit Data File Tech Doc.pdf. This file
contains lower level government data as well as state level totals. xxxx represents the year.

The program inserts the data into the BudgetData table.  Only items with a code
of the form xnn where x is the single letter representing the Object Code and nn
is the two digit number representing the function code.

Command-line arguments are as follows:

0.  A text file containing the DataSource parameters
1.  The text file containing the data to be downloaded
2.  The state code. (A two digit number.)
3.  The data year. 


