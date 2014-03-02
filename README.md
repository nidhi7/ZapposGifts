ZapposGifts
===========

Zappos challenge for getting combination of gifts

ZapTryAgain is a console application which requires 2 arguments: 
1) Number of gifts
2) Approximate amount the user wants to spend (in dollars)

My implementation:
I am getting sorted records from the API.
The API is called to get either
 - all the pages until the price of the last product on a page becomes greater than the amount specified by the user
   OR
 - the "maxNumberOfPages" pages 
 

"maxNumberOfPages" is an attribute that you can specify in the config.txt file. 
Ideally the first approach should be used (without the maxNumberofPages)
Explicitly keeping a check on the number of pages is only for testing purposes.
(Eg. Specify maxNumberOfPages=2 and amount user wants to spend = 10)
