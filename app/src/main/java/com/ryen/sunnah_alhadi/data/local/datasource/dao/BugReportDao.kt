package com.ryen.sunnah_alhadi.data.local.datasource.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ryen.sunnah_alhadi.data.local.datasource.entity.BugReportEntity

@Dao
interface BugReportDao {
    @Query("SELECT * FROM bug_reports WHERE isSynced = 0 ORDER BY timestamp DESC")
    suspend fun getPendingReports(): List<BugReportEntity>

    @Query("SELECT * FROM bug_reports ORDER BY timestamp DESC")
    suspend fun getAllReports(): List<BugReportEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBugReport(report: BugReportEntity)

    @Query("UPDATE bug_reports SET isSynced = 1 WHERE id = :reportId")
    suspend fun markAsSynced(reportId: String)

    @Query("DELETE FROM bug_reports WHERE isSynced = 1 AND createdAt < :cutoffTime")
    suspend fun deleteSyncedOldReports(cutoffTime: Long)

    @Query("SELECT COUNT(*) FROM bug_reports WHERE isSynced = 0")
    suspend fun getPendingReportsCount(): Int
}