Feature: Update todo

As a user, I want to update my to-do item so that I can keep the task details up to date

Background: 
    Given the API is responsive and contains only default todo objects

#Normal FLow 
Scenario Outline: Sucessfully update the fields of a todo
    When a todo of id "<id>" with the title "<title>", the doneStatus "<doneStatus>" and the description "<description>" with the name "<new_title>", doneStatus "<new_doneStatus>" and description "<new_description>"
    Then the status code 200 will be received 
    Then the todo with id "<id>" will exist in the database with the title "<new_title>", doneStatus "<new_doneStatus>" and description "<new_description>"

    Examples:
    | id | title           | doneStatus | description         | new_title        |  new_doneStatus | new_description |
    |  1 | file paperwork  | false      |                     | Shred Paper      |   false         | Shredding paper |
    |  2 | scan paperwork  | false      |                     | Cut Paperwork    |   true          | Cutting paper   |

#Alternate Flow 
Scenario Outline: Successfully update the description of a todo to an empty one 
    When a todo of id "<id>" with the title "<title>", the doneStatus "<doneStatus>" and the description "<description>" with the name "<new_title>", doneStatus "<new_doneStatus>" and an empty description "<new_description>"
    Then the status code 200 will be received 
    Then the todo with id "<id>" will exist in the database with the title "<new_title>", doneStatus "<new_doneStatus>" and description "<new_description>"

    Examples:
    | id | title             | doneStatus | description         | new_title        |  new_doneStatus | new_description |
    |  1 | file paperwork    | false      |                     | Shred Paper      |   false         |                 |
    |  2 | scan paperwork    | false      |                     | Staple Paperwork |   true          |                 |

#Error Flow 
Scenario Outline: Unsuccessfully update the title to an empty one 
    When a todo of id "<id>" with an empty title "<title>", the doneStatus "<doneStatus>" and the description "<description>" with the name "<new_title>", doneStatus "<new_doneStatus>" and description "<new_description>"
    Then a todo will not be updated
    Then the status code 400 will be received
    And the error message "<errorMessages>" will be raised 

    Examples:
    | id | title             | doneStatus | description         | new_title        |  new_doneStatus | new_description | errorMessages                               |
    |  1 | file paperwork    | false      | Filing paperwork    |                  |   false         |  Shredding      | Failed Validation: title : can not be empty |
    |  2 | scan paperwork    | false      | Filing paperwork    |                  |   true          |  Filing         | Failed Validation: title : can not be empty |

