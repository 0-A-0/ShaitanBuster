package com.example.scrolllist

data class LevelStates(
    val spawnTime: Long,
    val enemyScarecrowCounts: Int,
    val enemyCrowCounts: Int,
    val enemySmokeCounts: Int,
    val enemySpawnerCounts: Int,
    val speedScarecrowRange: IntRange = (0..0),
    val speedCrowRange: IntRange = (0..0),
    val speedSmokeRange: IntRange = (0..0),
    val speedMinionRange: IntRange = (0..0),
){
    val killableCount = enemyScarecrowCounts +enemyCrowCounts + enemySmokeCounts + enemySpawnerCounts
}
