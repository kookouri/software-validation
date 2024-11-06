Feature: Create todo

As a user, I want to create a new to-do item so that to keep track of my tasks

Background: 
    Given the API is responsive and contains only default todo objects

#Normal FLow 
Scenario Outline: Successfully create a new todo with all feilds
    When a new todo is created with the title "<title>", the doneStatus "<doneStatus>" and the description "<description>"
    Then the status code 201 will be received 
    Then a new todo exists in the database with the title "<title>", the doneStatus "<doneStatus>" and the description "<description>"

    Examples:
    | title               | doneStatus | description         | 
    | Stapling Paperwork  | false      | Filling paperwork   | 
    | Purchasing folders  | true       | Puttin away folders | 

#Alternate Flow 
Scenario Outline: Successfully create a new todo with only the title
    When a new todo is created with the title "<title>"
    Then the status code 201 will be received 
    Then a new todo exists in the database with the title "<title>" and the default status

    Examples:
    | title             |
    | Bind Paperwork    | 
    | Carry folders     | 


#Error Flow 
Scenario Outline: Unsuccessfully create a new todo with an empty title
    When a new todo is created with the title "<title>" and the done status "<doneStatus>" and the description "<description>"
    Then a todo will not be created
    Then the status code 400 will be received 
    And the error message "<errorMessages>" will be raised 

    Examples:
    | title             | doneStatus | description         | errorMessages                                  |
    |                   | false      | Filing paperwork    | Failed Validation: title : can not be empty    |
    |                   | true       | Puttin away folders | Failed Validation: title : can not be empty    |

