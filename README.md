# BattleBugs

This is a program of an autonomous BattleBug. A BattleBug would be put into a tournament with 3 other BattleBugs, and the last BattleBug alive would be the winner. On the grid, several power-ups were randomly placed, offering boosts to speed, strength, or defense. Every 40 moves, the border would be covered in rocks, making the grid get smaller as the game went on (the rocks were also a possible cause of death). The BattleBug needed a strategy and priorities, including determining when to run from other players, chase and attack them, avoid falling rocks, and obtain specific powerups.

Gary.java is my BattleBug player. I coded my BattleBug to avoid falling rocks, run away from stronger enemies nearby, chase and kill a weaker bug nearby, and get the nearest power-up (in that order), while always avoiding obstacles along the way.
