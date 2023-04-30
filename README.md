Data exchange protocol

First byte - message type:

New player:
------

| byte number | value   | description                       |
|-------------|---------|-----------------------------------|
| 1           | 0       | message type                      |
| 2           | N       | number of bytes for player name   |
| <3; 3+N)    | 0 - 127 | ASCII characters of player's name |
| 3+N         | 0 - 255 | Snake ID                          |
| 3+N + 1     | 0 - 255 | RED                               |
| 3+N + 2     | 0 - 255 | GREEN                             |
| 3+N + 3     | 0 - 255 | BLUE                              |
| 3+N + 4     | 0 - 255 | Snake's head X position           |
| 3+N + 5     | 0 - 255 | Snake's head Y position           |

Board delta
---------------------

| byte number | value   | description                                                                |
|-------------|---------|----------------------------------------------------------------------------|
| 1           | 1       | message type                                                               |
| ----------  | n > 0   | There might be more than one element operation in single websocket message |   
| n           | 0 - 2   | operation type: 0 for removal; 1 for addition; 2 for lost game             |
| n+1         | 0 - 255 | Parent element ID (snake) or 255 for Apple                                 |
| n+2         | 0 - 255 | X position                                                                 |
| n+3         | 0 - 255 | Y position                                                                 |

Pause game:
-----------

| byte number | value | description      |
|-------------|-------|------------------|
| 1           | 2     | message type     |
| 2           | 0-255 | pausing snake id |

End game:
------

| byte number | value | description  |
|-------------|-------|--------------|
| 1           | 3     | message type |
