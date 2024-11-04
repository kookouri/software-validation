Feature: Delete todo

As a user, I want to update a todo to reflect the current status 

Background: 
    Given the API is responsive and contains only default todo objects

#Normal FLow 
Scenario Outline: Sucessfully delete a todo 
    When a todo of id "<id>" with the title "<title>", the doneStatus "<doneStatus" and the description "<description>" is deleted from the system
    Then the status code 200 will be received 
    Then the todo with id "<id>" will be deleted from the database

    Examples:
    | id | title             | doneStatus | description         | 
    |  1 | Staple Paperwork  | false      | Filing paperwork    | 
    |  2 | File Paperwork    | false      | Filing paperwork    |

#Alternate Flow 
Scenario Outline: Sucessfully delete a new todo with only title
    When a new todo is deleted with the title "<title>" is deleted from the system
    Then the status code 200 will be received 
    Then the todo with title "<title>" will be deleted from the database

    Examples:
    | title             | 
    | Staple Paperwork  | 
    | File Paperwork    | 


#Error Flow 
Scenario Outline: Unsucessfully delete a todo thats already been deleted
    When a todo with an invalid "<id>" with the title "<title>", the doneStatus "<doneStatus" and the description "<description>" is deleted from the system
    Then the status code 404 will be received 

    Examples:
    | id  | title             | doneStatus | description         | 
    |  10 | Staple Paperwork  | false      | Filing paperwork    | 
    |  11 | File Paperwork    | false      | Filing paperwork    | 

