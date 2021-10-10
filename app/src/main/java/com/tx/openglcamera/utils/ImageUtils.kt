package com.tx.openglcamera.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.OutputStream
import java.lang.Exception

/**
 * create by xu.tian
 * @date 2021/10/9
 */
/**
 * Created by JokAr on 2017/10/16.
 */
object ImageUtil {

    /**
     * 返回相册或截屏中最新的一张图片
     */
    fun getLatestPhoto(context: Context): Pair<Long, String>? {
        //拍摄照片的地址
        val CAMERA_IMAGE_BUCKET_NAME = Environment.getExternalStorageDirectory().toString() + "/DCIM/Camera"
      /*  //截屏照片的地址
        val SCREENSHOTS_IMAGE_BUCKET_NAME = getScreenshotsPath()*/
        //拍摄照片的地址ID
        val CAMERA_IMAGE_BUCKET_ID = getBucketId(CAMERA_IMAGE_BUCKET_NAME)
        //截屏照片的地址ID
//        val SCREENSHOTS_IMAGE_BUCKET_ID = getBucketId(SCREENSHOTS_IMAGE_BUCKET_NAME)
        //查询路径和修改时间
        val projection = arrayOf(
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_MODIFIED)
        val selection = MediaStore.Images.Media.BUCKET_ID + " = ?"

        val selectionArgs = arrayOf(CAMERA_IMAGE_BUCKET_ID)
//        val selectionArgsForScreenshots = arrayOf(SCREENSHOTS_IMAGE_BUCKET_ID)

        var cameraPair: Pair<Long, String>? = null
        //检查camera文件夹，查询并排序
        var cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgs,
            MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
        if (cursor!!.moveToFirst()) {
            cameraPair = Pair(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))

        }
        if (!cursor.isClosed) {
            cursor.close()
        }
        if (cameraPair!=null){
            return cameraPair
        }
     /*   //检查Screenshots文件夹
        var screenshotsPair: Pair<Long, String>? = null
        //查询并排序
        cursor = context.contentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            selectionArgsForScreenshots,
            MediaStore.Files.FileColumns.DATE_MODIFIED + " DESC")
        if (cursor!!.moveToFirst()) {
            screenshotsPair = Pair(cursor.getLong(cursor.getColumnIndex(MediaStore.Images.Media.DATE_MODIFIED)),
                cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA)))
        }


        //对比
        if (cameraPair != null && screenshotsPair != null) {
            return if (cameraPair.first!! > screenshotsPair.first!!) {
                screenshotsPair = null
                cameraPair
            } else {
                cameraPair = null
                screenshotsPair
            }

        } else if (cameraPair != null && screenshotsPair == null) {
            return cameraPair

        } else if (cameraPair == null && screenshotsPair != null) {
            return screenshotsPair
        }*/

        return null
    }

    private fun getBucketId(path: String): String {
        return path.toLowerCase().hashCode().toString()
    }

/*    *//**
     * 获取截图路径
     *//*
    private fun getScreenshotsPath(): String {
        var path = Environment.getExternalStorageDirectory().toString() + "/DCIM/Screenshots"
        var file: File? = File(path)
        if (!file?.exists()!!) {
            path = Environment.getExternalStorageDirectory().toString() + "/Pictures/Screenshots"
        }
        file = null
        return path
    }*/

    fun save(context: Context , bitmap : Bitmap, path : String){
        val values = ContentValues()
        values.put(MediaStore.Images.Media.DESCRIPTION, "opengl tx camera")
        values.put(MediaStore.Images.Media.DISPLAY_NAME, path)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        values.put(MediaStore.Images.Media.TITLE, "Image.jpg")
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/")

        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        var outputStream: OutputStream? = null
        try {
            outputStream = context.contentResolver.openOutputStream(uri!!)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream!!.close()
        }catch (e: Exception){
            e.printStackTrace()
        }
    }

}