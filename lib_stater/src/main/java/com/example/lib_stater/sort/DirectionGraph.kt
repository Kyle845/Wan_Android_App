package com.example.lib_stater.sort

import java.util.*
import kotlin.collections.ArrayList

class DirectionGraph(private val mVertexCount: Int) {
    private val mAdjTable: Array<MutableList<Int>?> = arrayOfNulls(mVertexCount)

    init {
        for (i in 0 until mVertexCount) {
            mAdjTable[i] = mutableListOf<Int>()
        }
    }

    fun addEdge(u: Int, v: Int) {
        mAdjTable[u]?.add(v)
    }

    fun topologicalSort(): Vector<Int> {
        val inDegree = IntArray(mVertexCount)
        for (i in 0 until mVertexCount) {
            val temp = mAdjTable[i] as ArrayList<Int>
            for (node in temp) {
                inDegree[node]++
            }
        }
        val queue: Queue<Int> = LinkedList()
        for (i in 0 until mVertexCount) {
            if (inDegree[i] == 0) {
                queue.add(i)
            }
        }
        var cnt = 0
        val topOrder = Vector<Int>()
        while (!queue.isEmpty()) {
            val u = queue.poll()
            u?.let {
                topOrder.add(u)
                mAdjTable[u]?.forEach {node ->
                    if (--inDegree[node] == 0) {
                        queue.add(node)
                    }
                }
                cnt++
            }
        }
        check(cnt == mVertexCount) {
            "Exist a cycle in the graph"
        }
        return topOrder
    }
}