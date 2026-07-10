package com.example.scrolllist.domain.utils

class Accumulator(private val speed:Float) {
    var accumulator = 0f
        private set

    fun update(delta: Float, action:() -> Unit){
        if (speed <= 0f) {
            return
        }
        accumulator += delta
        if (accumulator > speed * 5) {
            accumulator = speed
        }
        while (accumulator >= speed){
            accumulator -= speed
            action()
        }
    }
    fun reset(){
        accumulator = 0f
    }
}