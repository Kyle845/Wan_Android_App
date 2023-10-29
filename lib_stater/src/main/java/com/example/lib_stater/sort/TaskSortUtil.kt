package com.example.lib_stater.sort

import android.os.Build
import android.util.ArraySet
import androidx.annotation.RequiresApi
import com.example.lib_stater.task.Task
import com.example.lib_stater.utils.DispatcherLog

object TaskSortUtil {
    private val sNewTasksHigh: MutableList<Task> = ArrayList()

    @RequiresApi(Build.VERSION_CODES.M)
    @Synchronized
    fun getSortResult(
        originTasks: List<Task>,
        clsLaunchTasks: List<Class<out Task>>
    ): List<Task> {
            val makeTime = System.currentTimeMillis()
            val dependSet: MutableSet<Int> = ArraySet()
            val graph = DirectionGraph(originTasks.size)

            for (i in originTasks.indices) {
                val task = originTasks[i]
                if (task.isSend || task.dependsOn().isNullOrEmpty()) {
                    continue
                }
                task.dependsOn()?.let {list ->
                    for (clazz in list) {
                        clazz?.let { cls ->
                            val indexOfDepend = getIndexOfTask(originTasks, clsLaunchTasks, cls)
                            check(indexOfDepend >= 0) {
                                task.javaClass.simpleName +
                                        " depends on " + cls?.simpleName + " can not be found in task list "
                            }
                                dependSet.add(indexOfDepend)
                                graph.addEdge(indexOfDepend, i)
                        }
                    }
                }
            }
        val indexList: List<Int> = graph.topologicalSort()
        val newTasksAll = getResultTasks(originTasks, dependSet, indexList)
        DispatcherLog.i("task analyse cost makeTime " + (System.currentTimeMillis() - makeTime))
        printAllTaskName(newTasksAll,false)
        return newTasksAll

    }

    private fun printAllTaskName(newTasksAll: List<Task>, isPrintName: Boolean) {
        if (!isPrintName) {
            return
        }
        for (task in newTasksAll) {
            DispatcherLog.i(task.javaClass.simpleName)
        }
    }

    val tasksHigh: List<Task>
        get() = sNewTasksHigh


    private fun getResultTasks(
        originTasks: List<Task>,
        dependSet: Set<Int>,
        indexList: List<Int>
    ): List<Task> {
        val newTaskAll: MutableList<Task> = ArrayList(originTasks.size)

        val newTasksDepended: MutableList<Task> = ArrayList()

        val newTasksWithOutDepend: MutableList<Task> = ArrayList()

        val newTasksRunAsSoon: MutableList<Task> = ArrayList()

        for (index in indexList) {
            if (dependSet.contains(index)) {
                newTasksDepended.add(originTasks[index])
            } else {
                val task = originTasks[index]
                if (task.needRunAsSoon()) {
                    newTasksRunAsSoon.add(task)
                } else {
                    newTasksWithOutDepend.add(task)
                }
            }
        }
        sNewTasksHigh.addAll(newTasksDepended)
        sNewTasksHigh.addAll(newTasksRunAsSoon)
        newTaskAll.addAll(sNewTasksHigh)
        newTaskAll.addAll(newTasksWithOutDepend)
        return newTaskAll
    }
    private fun getIndexOfTask(
        originTasks: List<Task>,
        clsLaunchTasks: List<Class<out Task>>,
        cls:Class<*>
    ): Int {
        val index = clsLaunchTasks.indexOf(cls)
        if (index >= 0) {
            return index
        }

        val size = originTasks.size
        for (i in 0 until size) {
            if (cls.simpleName == originTasks[i].javaClass.simpleName) {
                return i
            }
        }
        return index
    }
}