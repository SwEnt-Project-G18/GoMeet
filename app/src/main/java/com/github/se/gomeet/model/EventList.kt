package com.github.se.gomeet.model

class ToDoList(private val todos: List<Event>) {
    fun getAllTask(): List<Event> = todos

    fun getFilteredTask(query: String): List<Event> =
        todos.filter {
            it.title.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
}