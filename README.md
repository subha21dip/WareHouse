# WareHouse

#Iteration 1
Assumption : Each product has a priority based on which product count is maximized. The higher the priority number, the lower the priority.
Future Work : Auth implementation for the APIs. Log addition. Test case addition. Currently when we upload product json, product quantities are calculated based on the articles present in the inventory. But same should happen when we upload new inventory json, currently we only update articles on inventory upload.

# Iteration 2
Added unit test for product and inventory GET api.
Added exception handling and custom exceptions.
Added info, debug and error logs.
Updated product get call to return corresponding articles too.
