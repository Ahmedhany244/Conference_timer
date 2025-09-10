// API Response Types based on Spring Boot DTOs

// User Types
export interface User {
  id: number;
  name: string;
  email: string;
}

export interface UserDtoRequest {
  name: string;
  email: string;
  password: string;
}

export interface UserDtoResponse {
  id: number;
  name: string;
  email: string;
}

export interface UserDtoLoginRequest {
  email: string;
  password: string;
}

export interface UserDtoLoginResponse {
  token: string;
  role: string;
}

// Admin Types
export interface AdminDtoRequest {
  staffName: string;
  email: string;
  password: string;
}

export interface AdminDtoResponse {
  id: number;
  name: string;
  email: string;
}

// Event Types
export interface Event {
  id: number;
  eventName: string;
  eventStartTime: string;
  eventEndTime: string;
}

export interface EventDtoRequest {
  eventName: string;
  eventStartTime: string;
  eventEndTime: string;
}

export interface EventDtoResponse {
  id: number;
  eventName: string;
  eventStartTime: string;
  eventEndTime: string;
}

// Registration Types
export interface RegistrationDtoResponse {
  registrationId: number;
  eventId: number;
  eventName: string;
  code: string;
  qrBase64: string;
}

// Attendance Types
export interface AttendanceDtoResponse {
  registrationId: number;
  creditHours: number;
  status: string;
}

export interface EventAttendeeResponse {
  registrationId: number;
  userId: number;
  userName: string;
  userEmail: string;
  registrationCode: string;
  currentStatus: string;
  lastActivity: string;
  totalCreditHours: number;
  lastAction: string;
}

export interface ScanDtoRequest {
  code: string;
  action?: "CHECKIN" | "PAUSE" | "RESUME" | "CHECKOUT";
  idempotencyKey?: string;
}

// Export Types
export interface AttendeeExportResponse {
  csvContent: string;
  filename: string;
}

// Dashboard Types
export interface DashboardStatsResponse {
  totalEvents: number;
  totalAttendees: number;
  totalRegistrations: number;
}

// Attendee Dashboard Types
export interface AttendeeDashboardStatsResponse {
  upcomingEvents: number;
  totalAttendanceHours: number;
  qrScans: number;
  eventsAttended: number;
  recentActivity: RecentActivityResponse[];
}

// Recent Activity Types
export interface RecentActivityResponse {
  activityType: string;
  eventName: string;
  timestamp: string;
  status: string;
}

// Bulk Operations
export interface BulkCheckoutResponse {
  message: string;
  checkedOutCount: number;
  totalAttendees: number;
}

// Form Data Types
export interface AdminRegisterFormData {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
  validationCode: string;
}

// UI State Types
export interface AuthState {
  user: User | null;
  token: string | null;
  role: "USER" | "ADMIN" | null;
  isLoading: boolean;
}

export interface ApiError {
  message: string;
  status: number;
  timestamp: string;
}

// Navigation Types
export interface NavItem {
  title: string;
  href: string;
  icon?: string;
  badge?: string;
}

// Form Types
export interface LoginFormData {
  email: string;
  password: string;
}

export interface RegisterFormData {
  name: string;
  email: string;
  password: string;
  confirmPassword: string;
}

export interface EventFormData {
  eventName: string;
  eventStartTime: string;
  eventEndTime: string;
}

// Advanced Analytics Types
export interface AnalyticsOverviewResponse {
  averageAttendanceTimeHours: number;
  medianAttendanceTimeHours: number;
  totalActiveAttendees: number;
  overallAttendanceRate: number;
  totalScanEvents: number;
  averageSessionDuration: number;
}

export interface EventAnalyticsResponse {
  eventId: number;
  eventName: string;
  totalRegistrations: number;
  totalAttendees: number;
  attendanceRate: number;
  averageAttendanceTimeHours: number;
  totalAttendanceHours: number;
  totalCheckIns: number;
  totalCheckOuts: number;
  averageSessionDuration: number;
  eventStartTime: string;
  eventEndTime: string;
}

export interface TimeAnalyticsResponse {
  hourlyActivity: HourlyActivityResponse[];
  dailyActivity: DailyActivityResponse[];
  peakCheckInTime: PeakTimeResponse;
  peakCheckOutTime: PeakTimeResponse;
  averageSessionLength: number;
  longestSession: number;
  shortestSession: number;
}

export interface HourlyActivityResponse {
  hour: number;
  checkInCount: number;
  checkOutCount: number;
  totalActivity: number;
}

export interface DailyActivityResponse {
  date: string;
  totalActivity: number;
  uniqueAttendees: number;
  totalHours: number;
}

export interface PeakTimeResponse {
  hour: number;
  activityCount: number;
  timeLabel: string;
}

export interface AttendanceRateBreakdownResponse {
  overallAttendanceRate: number;
  eventRates: EventRateResponse[];
  rateDistribution: RateRangeResponse;
  topAttendees: UserAttendanceResponse[];
  averageRegistrationToAttendanceTime: number;
}

export interface EventRateResponse {
  eventId: number;
  eventName: string;
  registrations: number;
  attendees: number;
  attendanceRate: number;
  category: string;
}

export interface RateRangeResponse {
  excellent: number; // 90-100%
  good: number; // 70-89%
  average: number; // 50-69%
  poor: number; // 0-49%
}

export interface UserAttendanceResponse {
  userId: number;
  userName: string;
  userEmail: string;
  eventsRegistered: number;
  eventsAttended: number;
  attendanceRate: number;
  totalHours: number;
}
