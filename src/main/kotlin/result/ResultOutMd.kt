package result

import model.ApkFileType
import model.ResultDiffEnum
import utils.*
import java.io.File
import kotlin.io.path.isDirectory
import kotlin.io.path.pathString

/**
 * Out apk result save to md file.
 * @author petterp
 */
class ResultOutMd : ResultHelper() {

    override fun start() {
        val diffOutPath = result.diffOutPath
        var path = diffOutPath.pathString
        if (diffOutPath.isDirectory()) {
            path += "/apk_size_diff.md"
        }
        File(path).createFileIfNoExists().outputStream().use {
            val builder = StringBuilder()
            builder.mdHeader(4, "Apk Size Diff Analysis 🧩")
            builder.mdTable(
                listOf(
                    "Metric",
                    "Base Apk",
                    "Target Apk",
                    "Diff",
                    "Status"
                ),
                addMdList(ApkFileType.APK),
                addMdList(ApkFileType.DEX),
                addMdList(ApkFileType.RES),
                addMdList(ApkFileType.LIB),
                addMdList(ApkFileType.ARSC),
                addMdList(ApkFileType.OTHER),
            )
            if (result.isBeyondThreshold) {
                builder.mdReference("本次扫描未通过，包大小超出限定阈值，请检查你的改动代码。\n")
            }
            it.write(builder.toString().toByteArray())
        }
    }

    private fun addMdList(fileType: ApkFileType): List<String> {
        return listOf(
            fileType.title,
            "${result.baseMap[fileType]?.size?.unit ?: 0}",
            "${result.curMap[fileType]?.size?.unit ?: 0}",
            "${result.diffMap[fileType]?.unit ?: 0}".mdAddBold(true),
            result.tMap.getValue(fileType).status
        )
    }
}