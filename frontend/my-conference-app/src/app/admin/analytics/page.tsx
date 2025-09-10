"use client";

import { useState, useEffect } from "react";
import { useRouter } from "next/navigation";
import { Button } from "@/components/ui/button";
import {
  BarChart3,
  Users,
  Calendar,
  Clock,
  TrendingUp,
  Activity,
  Download,
  RefreshCw,
  CheckCircle,
  UserCheck,
  QrCode,
  AlertCircle,
} from "lucide-react";
import { apiClient } from "@/lib/api";
import { API_ENDPOINTS } from "@/lib/api";
import type {
  DashboardStatsResponse,
  AnalyticsOverviewResponse,
  EventAnalyticsResponse,
  TimeAnalyticsResponse,
  AttendanceRateBreakdownResponse,
} from "@/lib/types";

interface AdvancedAnalyticsData {
  overview: AnalyticsOverviewResponse;
  eventAnalytics: EventAnalyticsResponse[];
  timePatterns: TimeAnalyticsResponse;
  attendanceRates: AttendanceRateBreakdownResponse;
  dashboardStats: DashboardStatsResponse;
}

export default function AnalyticsPage() {
  const router = useRouter();
  const [analyticsData, setAnalyticsData] =
    useState<AdvancedAnalyticsData | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selectedView, setSelectedView] = useState<
    "overview" | "events" | "time" | "rates"
  >("overview");

  // Authentication check
  useEffect(() => {
    const token = localStorage.getItem("token");
    const role = localStorage.getItem("role");
    if (!token || role !== "ADMIN") {
      router.push("/admin/login");
      return;
    }
    fetchAnalyticsData();
  }, [router]);

  const fetchAnalyticsData = async () => {
    try {
      setIsLoading(true);
      setError(null);

      // Fetch all analytics data in parallel for better performance
      const [
        overviewResponse,
        eventAnalyticsResponse,
        timePatternsResponse,
        attendanceRatesResponse,
        dashboardStatsResponse,
      ] = await Promise.all([
        apiClient.get<AnalyticsOverviewResponse>(
          API_ENDPOINTS.ANALYTICS_OVERVIEW
        ),
        apiClient.get<EventAnalyticsResponse[]>(API_ENDPOINTS.ANALYTICS_EVENTS),
        apiClient.get<TimeAnalyticsResponse>(
          API_ENDPOINTS.ANALYTICS_TIME_PATTERNS
        ),
        apiClient.get<AttendanceRateBreakdownResponse>(
          API_ENDPOINTS.ANALYTICS_ATTENDANCE_RATES
        ),
        apiClient.get<DashboardStatsResponse>(API_ENDPOINTS.DASHBOARD_STATS),
      ]);

      setAnalyticsData({
        overview: overviewResponse.data,
        eventAnalytics: eventAnalyticsResponse.data,
        timePatterns: timePatternsResponse.data,
        attendanceRates: attendanceRatesResponse.data,
        dashboardStats: dashboardStatsResponse.data,
      });
    } catch (err: any) {
      console.error("Failed to fetch analytics data:", err);
      setError("Failed to load advanced analytics data. Please try again.");
    } finally {
      setIsLoading(false);
    }
  };

  const getTopPerformingEvents = () => {
    if (!analyticsData?.eventAnalytics) return [];

    return analyticsData.eventAnalytics
      .sort((a, b) => b.attendanceRate - a.attendanceRate)
      .slice(0, 5);
  };

  const getPeakActivityHours = () => {
    if (!analyticsData?.timePatterns?.hourlyActivity) return [];

    return analyticsData.timePatterns.hourlyActivity
      .sort((a, b) => b.totalActivity - a.totalActivity)
      .slice(0, 6);
  };

  const formatDuration = (hours: number): string => {
    const wholeHours = Math.floor(hours);
    const minutes = Math.round((hours - wholeHours) * 60);
    return `${wholeHours}h ${minutes}m`;
  };

  if (isLoading) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 to-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="mb-10">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h1 className="text-4xl font-bold text-gray-900 mb-2">
                  Analytics Dashboard
                </h1>
                <p className="text-lg text-gray-600">
                  Loading conference insights...
                </p>
              </div>
            </div>

            <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-200">
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
                {[1, 2, 3, 4].map((i) => (
                  <div key={i} className="text-center animate-pulse">
                    <div className="w-16 h-16 bg-gray-200 rounded-full mx-auto mb-4"></div>
                    <div className="h-8 bg-gray-200 rounded w-20 mx-auto mb-1"></div>
                    <div className="h-4 bg-gray-200 rounded w-24 mx-auto"></div>
                  </div>
                ))}
              </div>
            </div>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-10">
            {[1, 2, 3].map((i) => (
              <div
                key={i}
                className="bg-white rounded-2xl shadow-lg p-6 border border-gray-200 animate-pulse"
              >
                <div className="h-4 bg-gray-200 rounded w-16 mb-4"></div>
                <div className="h-6 bg-gray-200 rounded w-12 mb-2"></div>
                <div className="h-3 bg-gray-200 rounded w-20"></div>
              </div>
            ))}
          </div>
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="min-h-screen bg-gradient-to-br from-slate-50 to-gray-100">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
          <div className="mb-10">
            <div className="flex items-center justify-between mb-6">
              <div>
                <h1 className="text-4xl font-bold text-gray-900 mb-2">
                  Analytics Dashboard
                </h1>
                <p className="text-lg text-red-600">
                  Failed to load analytics data
                </p>
              </div>
              <Button
                onClick={fetchAnalyticsData}
                className="bg-red-600 hover:bg-red-700 text-white"
              >
                <RefreshCw className="mr-2 h-4 w-4" />
                Try Again
              </Button>
            </div>

            <div className="bg-white rounded-2xl shadow-lg p-8 border border-red-200">
              <div className="text-center">
                <AlertCircle className="h-16 w-16 text-red-500 mx-auto mb-4" />
                <h3 className="text-xl font-semibold text-gray-900 mb-2">
                  Unable to Load Analytics
                </h3>
                <p className="text-gray-600 mb-4">{error}</p>
                <Button onClick={fetchAnalyticsData} variant="outline">
                  <RefreshCw className="mr-2 h-4 w-4" />
                  Retry Loading
                </Button>
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

  if (!analyticsData) {
    return null;
  }

  const topEvents = getTopPerformingEvents();
  const peakHours = getPeakActivityHours();

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 to-gray-100">
      <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Header */}
        <div className="mb-10">
          <div className="flex items-center justify-between mb-6">
            <div>
              <h1 className="text-4xl font-bold text-gray-900 mb-2">
                Advanced Analytics Dashboard
              </h1>
              <p className="text-lg text-gray-600">
                Comprehensive conference insights and performance metrics
              </p>
            </div>
            <div className="flex items-center space-x-3">
              <div className="flex bg-white rounded-lg p-1 shadow-sm">
                {[
                  { key: "overview", label: "Overview", icon: BarChart3 },
                  { key: "events", label: "Events", icon: Calendar },
                  { key: "time", label: "Time", icon: Clock },
                  { key: "rates", label: "Rates", icon: TrendingUp },
                ].map((view) => (
                  <button
                    key={view.key}
                    onClick={() => setSelectedView(view.key as any)}
                    className={`px-3 py-2 text-sm font-medium rounded-md transition-colors flex items-center space-x-2 ${
                      selectedView === view.key
                        ? "bg-blue-600 text-white"
                        : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
                    }`}
                  >
                    <view.icon className="h-4 w-4" />
                    <span>{view.label}</span>
                  </button>
                ))}
              </div>
              <Button
                onClick={fetchAnalyticsData}
                variant="outline"
                className="bg-white shadow-sm hover:shadow-md transition-shadow"
              >
                <RefreshCw className="mr-2 h-4 w-4" />
                Refresh
              </Button>
              <Button
                variant="default"
                className="bg-blue-600 hover:bg-blue-700 shadow-sm hover:shadow-md transition-shadow"
              >
                <Download className="mr-2 h-4 w-4" />
                Export Report
              </Button>
            </div>
          </div>

          {/* Key Stats Overview */}
          <div className="bg-white rounded-2xl shadow-lg p-8 border border-gray-200">
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-8">
              <div className="text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-blue-100 rounded-full mb-4">
                  <BarChart3 className="h-8 w-8 text-blue-600" />
                </div>
                <div className="text-3xl font-bold text-gray-900 mb-1">
                  {formatDuration(
                    analyticsData?.overview?.averageAttendanceTimeHours || 0
                  )}
                </div>
                <p className="text-sm font-medium text-gray-600">
                  Average Attendance Time
                </p>
                <p className="text-xs text-gray-500 mt-1">Mean duration</p>
              </div>

              <div className="text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-green-100 rounded-full mb-4">
                  <TrendingUp className="h-8 w-8 text-green-600" />
                </div>
                <div className="text-3xl font-bold text-gray-900 mb-1">
                  {(
                    analyticsData?.overview?.overallAttendanceRate || 0
                  ).toFixed(1)}
                  %
                </div>
                <p className="text-sm font-medium text-gray-600">
                  Overall Attendance Rate
                </p>
                <p className="text-xs text-gray-500 mt-1">
                  Active vs registered
                </p>
              </div>

              <div className="text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-purple-100 rounded-full mb-4">
                  <Clock className="h-8 w-8 text-purple-600" />
                </div>
                <div className="text-3xl font-bold text-gray-900 mb-1">
                  {formatDuration(
                    analyticsData?.overview?.medianAttendanceTimeHours || 0
                  )}
                </div>
                <p className="text-sm font-medium text-gray-600">Median Time</p>
                <p className="text-xs text-gray-500 mt-1">Middle value</p>
              </div>

              <div className="text-center">
                <div className="inline-flex items-center justify-center w-16 h-16 bg-orange-100 rounded-full mb-4">
                  <Activity className="h-8 w-8 text-orange-600" />
                </div>
                <div className="text-3xl font-bold text-gray-900 mb-1">
                  {(
                    analyticsData?.overview?.totalScanEvents || 0
                  ).toLocaleString()}
                </div>
                <p className="text-sm font-medium text-gray-600">
                  Total Scan Events
                </p>
                <p className="text-xs text-gray-500 mt-1">All activities</p>
              </div>
            </div>
          </div>
        </div>

        {/* View-specific Content */}
        {selectedView === "overview" && (
          <>
            {/* Secondary Metrics */}
            <div className="grid grid-cols-1 lg:grid-cols-3 gap-8 mb-10">
              <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-200">
                <div className="flex items-center justify-between mb-4">
                  <div className="inline-flex items-center justify-center w-12 h-12 bg-indigo-100 rounded-lg">
                    <Calendar className="h-6 w-6 text-indigo-600" />
                  </div>
                  <span className="text-sm font-medium text-gray-500">
                    Events
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900 mb-1">
                  {analyticsData?.dashboardStats?.totalEvents || 0}
                </div>
                <p className="text-sm text-gray-600">Active conferences</p>
              </div>

              <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-200">
                <div className="flex items-center justify-between mb-4">
                  <div className="inline-flex items-center justify-center w-12 h-12 bg-emerald-100 rounded-lg">
                    <UserCheck className="h-6 w-6 text-emerald-600" />
                  </div>
                  <span className="text-sm font-medium text-gray-500">
                    Active Attendees
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900 mb-1">
                  {analyticsData?.overview?.totalActiveAttendees || 0}
                </div>
                <p className="text-sm text-gray-600">With attendance time</p>
              </div>

              <div className="bg-white rounded-2xl shadow-lg p-6 border border-gray-200">
                <div className="flex items-center justify-between mb-4">
                  <div className="inline-flex items-center justify-center w-12 h-12 bg-rose-100 rounded-lg">
                    <Clock className="h-6 w-6 text-rose-600" />
                  </div>
                  <span className="text-sm font-medium text-gray-500">
                    Avg Session
                  </span>
                </div>
                <div className="text-2xl font-bold text-gray-900 mb-1">
                  {formatDuration(
                    analyticsData?.overview?.averageSessionDuration || 0
                  )}
                </div>
                <p className="text-sm text-gray-600">Per attendee</p>
              </div>
            </div>
          </>
        )}

        {/* View-specific Analytics Sections */}
        {selectedView === "events" && (
          <div className="space-y-8">
            <div className="bg-white rounded-2xl shadow-lg border border-gray-200">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-xl font-bold text-gray-900 mb-2">
                  Event Performance Analytics
                </h3>
                <p className="text-gray-600">
                  Detailed breakdown of each event's performance
                </p>
              </div>
              <div className="p-6">
                <div className="space-y-4">
                  {(analyticsData?.eventAnalytics || []).map((event) => (
                    <div
                      key={event.eventId}
                      className="p-6 bg-gray-50 rounded-xl"
                    >
                      <div className="flex items-center justify-between mb-4">
                        <h4 className="text-lg font-semibold text-gray-900">
                          {event.eventName}
                        </h4>
                        <span
                          className={`px-3 py-1 rounded-full text-sm font-medium ${
                            event.attendanceRate >= 80
                              ? "bg-green-100 text-green-800"
                              : event.attendanceRate >= 60
                              ? "bg-yellow-100 text-yellow-800"
                              : "bg-red-100 text-red-800"
                          }`}
                        >
                          {event.attendanceRate.toFixed(1)}% attendance
                        </span>
                      </div>
                      <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
                        <div>
                          <p className="text-sm text-gray-500">Registrations</p>
                          <p className="text-xl font-bold text-gray-900">
                            {event.totalRegistrations}
                          </p>
                        </div>
                        <div>
                          <p className="text-sm text-gray-500">Attendees</p>
                          <p className="text-xl font-bold text-gray-900">
                            {event.totalAttendees}
                          </p>
                        </div>
                        <div>
                          <p className="text-sm text-gray-500">Avg Time</p>
                          <p className="text-xl font-bold text-gray-900">
                            {formatDuration(event.averageAttendanceTimeHours)}
                          </p>
                        </div>
                        <div>
                          <p className="text-sm text-gray-500">Total Hours</p>
                          <p className="text-xl font-bold text-gray-900">
                            {formatDuration(event.totalAttendanceHours)}
                          </p>
                        </div>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            </div>
          </div>
        )}

        {selectedView === "time" && (
          <div className="space-y-8">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div className="bg-white rounded-2xl shadow-lg border border-gray-200">
                <div className="p-6 border-b border-gray-200">
                  <h3 className="text-xl font-bold text-gray-900 mb-2">
                    Peak Activity Hours
                  </h3>
                  <p className="text-gray-600">
                    Most active times throughout the day
                  </p>
                </div>
                <div className="p-6">
                  <div className="space-y-3">
                    {peakHours.map((hour) => (
                      <div
                        key={hour.hour}
                        className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                      >
                        <span className="font-medium text-gray-900">
                          {hour.hour}:00 - {hour.hour + 1}:00
                        </span>
                        <div className="flex items-center space-x-2">
                          <div className="w-20 bg-gray-200 rounded-full h-2">
                            <div
                              className="bg-blue-600 h-2 rounded-full"
                              style={{
                                width: `${
                                  (hour.totalActivity /
                                    Math.max(
                                      ...peakHours.map((h) => h.totalActivity)
                                    )) *
                                  100
                                }%`,
                              }}
                            />
                          </div>
                          <span className="text-sm font-medium text-gray-600">
                            {hour.totalActivity}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-2xl shadow-lg border border-gray-200">
                <div className="p-6 border-b border-gray-200">
                  <h3 className="text-xl font-bold text-gray-900 mb-2">
                    Session Statistics
                  </h3>
                  <p className="text-gray-600">Attendance duration insights</p>
                </div>
                <div className="p-6 space-y-6">
                  <div className="text-center">
                    <div className="text-3xl font-bold text-blue-600 mb-2">
                      {formatDuration(
                        analyticsData?.timePatterns?.averageSessionLength || 0
                      )}
                    </div>
                    <p className="text-gray-600">Average Session Length</p>
                  </div>
                  <div className="grid grid-cols-2 gap-4">
                    <div className="text-center">
                      <div className="text-xl font-bold text-green-600 mb-1">
                        {formatDuration(
                          analyticsData?.timePatterns?.longestSession || 0
                        )}
                      </div>
                      <p className="text-sm text-gray-600">Longest</p>
                    </div>
                    <div className="text-center">
                      <div className="text-xl font-bold text-orange-600 mb-1">
                        {formatDuration(
                          analyticsData?.timePatterns?.shortestSession || 0
                        )}
                      </div>
                      <p className="text-sm text-gray-600">Shortest</p>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {selectedView === "rates" && (
          <div className="space-y-8">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-8">
              <div className="bg-white rounded-2xl shadow-lg border border-gray-200">
                <div className="p-6 border-b border-gray-200">
                  <h3 className="text-xl font-bold text-gray-900 mb-2">
                    Rate Distribution
                  </h3>
                  <p className="text-gray-600">
                    Events by performance category
                  </p>
                </div>
                <div className="p-6">
                  <div className="space-y-4">
                    {[
                      {
                        label: "Excellent (90%+)",
                        count:
                          analyticsData?.attendanceRates?.rateDistribution
                            ?.excellent || 0,
                        color: "green",
                      },
                      {
                        label: "Good (70-89%)",
                        count:
                          analyticsData?.attendanceRates?.rateDistribution
                            ?.good || 0,
                        color: "blue",
                      },
                      {
                        label: "Average (50-69%)",
                        count:
                          analyticsData?.attendanceRates?.rateDistribution
                            ?.average || 0,
                        color: "yellow",
                      },
                      {
                        label: "Poor (0-49%)",
                        count:
                          analyticsData?.attendanceRates?.rateDistribution
                            ?.poor || 0,
                        color: "red",
                      },
                    ].map((category) => (
                      <div
                        key={category.label}
                        className="flex items-center justify-between"
                      >
                        <span className="text-gray-700">{category.label}</span>
                        <div className="flex items-center space-x-2">
                          <div
                            className={`w-3 h-3 rounded-full bg-${category.color}-500`}
                          />
                          <span className="font-medium text-gray-900">
                            {category.count}
                          </span>
                        </div>
                      </div>
                    ))}
                  </div>
                </div>
              </div>

              <div className="bg-white rounded-2xl shadow-lg border border-gray-200">
                <div className="p-6 border-b border-gray-200">
                  <h3 className="text-xl font-bold text-gray-900 mb-2">
                    Top Attendees
                  </h3>
                  <p className="text-gray-600">Most active participants</p>
                </div>
                <div className="p-6">
                  <div className="space-y-3">
                    {(analyticsData?.attendanceRates?.topAttendees || [])
                      .slice(0, 5)
                      .map((attendee, index) => (
                        <div
                          key={attendee.userId}
                          className="flex items-center justify-between p-3 bg-gray-50 rounded-lg"
                        >
                          <div className="flex items-center space-x-3">
                            <div className="w-8 h-8 bg-blue-100 rounded-full flex items-center justify-center text-sm font-bold text-blue-600">
                              {index + 1}
                            </div>
                            <div>
                              <p className="font-medium text-gray-900">
                                {attendee.userName}
                              </p>
                              <p className="text-xs text-gray-500">
                                {attendee.eventsAttended}/
                                {attendee.eventsRegistered} events
                              </p>
                            </div>
                          </div>
                          <div className="text-right">
                            <p className="font-bold text-gray-900">
                              {formatDuration(attendee.totalHours)}
                            </p>
                            <p className="text-xs text-gray-500">
                              {attendee.attendanceRate.toFixed(1)}%
                            </p>
                          </div>
                        </div>
                      ))}
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}

        {/* Overview Default Content */}
        {selectedView === "overview" && (
          <div className="grid grid-cols-1 xl:grid-cols-2 gap-8 mb-10">
            {/* Top Performing Events */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-200">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-xl font-bold text-gray-900 mb-2">
                  Top Performing Events
                </h3>
                <p className="text-gray-600">
                  Events ranked by attendance rate and engagement
                </p>
              </div>
              <div className="p-6">
                {topEvents.length > 0 ? (
                  <div className="space-y-4">
                    {topEvents.map((event, index) => (
                      <div
                        key={event.eventId}
                        className="flex items-center justify-between p-4 bg-gray-50 rounded-xl hover:bg-gray-100 transition-colors"
                      >
                        <div className="flex items-center space-x-4">
                          <div className="flex items-center justify-center w-8 h-8 rounded-full bg-gradient-to-r from-blue-500 to-purple-600 text-white text-sm font-bold">
                            {index + 1}
                          </div>
                          <div>
                            <h4 className="font-semibold text-gray-900">
                              {event.eventName}
                            </h4>
                            <p className="text-sm text-gray-600">
                              {event.totalAttendees} attendees •{" "}
                              {formatDuration(event.totalAttendanceHours)} total
                            </p>
                          </div>
                        </div>
                        <div className="text-right">
                          <div className="text-lg font-bold text-gray-900">
                            {event.attendanceRate.toFixed(1)}%
                          </div>
                          <div className="text-xs text-gray-500">
                            attendance
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                ) : (
                  <div className="text-center py-12 text-gray-500">
                    <Calendar className="h-16 w-16 mx-auto mb-4 opacity-30" />
                    <p className="text-lg">No events data available</p>
                  </div>
                )}
              </div>
            </div>

            {/* Quick Insights */}
            <div className="bg-white rounded-2xl shadow-lg border border-gray-200">
              <div className="p-6 border-b border-gray-200">
                <h3 className="text-xl font-bold text-gray-900 mb-2">
                  Quick Insights
                </h3>
                <p className="text-gray-600">
                  Key performance indicators at a glance
                </p>
              </div>
              <div className="p-6 space-y-6">
                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="inline-flex items-center justify-center w-10 h-10 bg-blue-100 rounded-lg">
                      <TrendingUp className="h-5 w-5 text-blue-600" />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-700">
                        Most Active Event
                      </p>
                      <p className="text-xs text-gray-500">
                        Highest attendance
                      </p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-gray-900">
                      {topEvents[0]?.eventName.substring(0, 15) || "N/A"}...
                    </p>
                    <p className="text-xs text-gray-500">
                      {topEvents[0]?.attendanceRate.toFixed(1)}% rate
                    </p>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="inline-flex items-center justify-center w-10 h-10 bg-green-100 rounded-lg">
                      <Clock className="h-5 w-5 text-green-600" />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-700">
                        Peak Activity Time
                      </p>
                      <p className="text-xs text-gray-500">Most check-ins</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-gray-900">
                      {analyticsData?.timePatterns?.peakCheckInTime
                        ?.timeLabel || "N/A"}
                    </p>
                    <p className="text-xs text-gray-500">
                      {analyticsData?.timePatterns?.peakCheckInTime
                        ?.activityCount || 0}{" "}
                      activities
                    </p>
                  </div>
                </div>

                <div className="flex items-center justify-between">
                  <div className="flex items-center space-x-3">
                    <div className="inline-flex items-center justify-center w-10 h-10 bg-purple-100 rounded-lg">
                      <BarChart3 className="h-5 w-5 text-purple-600" />
                    </div>
                    <div>
                      <p className="text-sm font-medium text-gray-700">
                        Registration to Attendance
                      </p>
                      <p className="text-xs text-gray-500">Average time</p>
                    </div>
                  </div>
                  <div className="text-right">
                    <p className="font-bold text-gray-900">
                      {formatDuration(
                        analyticsData?.attendanceRates
                          ?.averageRegistrationToAttendanceTime || 0
                      )}
                    </p>
                    <p className="text-xs text-gray-500">avg delay</p>
                  </div>
                </div>
              </div>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
