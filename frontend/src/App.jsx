import React from "react";
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import { AuthProvider, useAuth } from "./context/AuthContext";
import ProtectedRoute from "./components/ProtectedRoute";
import NavBar from "./components/NavBar";
import SosButton from "./pages/sos/SosButton";

// Auth / Profile (Workflow 1)
import LoginPage from "./pages/auth/LoginPage";
import RegisterPage from "./pages/auth/RegisterPage";
import ProfilePage from "./pages/profile/ProfilePage";
import TrustedContactsPage from "./pages/profile/TrustedContactsPage";

// Journey Mode (Workflow 2)
import JourneyModePage from "./pages/journey/JourneyModePage";
import TrackJourneyPage from "./pages/journey/TrackJourneyPage";

// SOS history (Workflow 3) - SosButton itself is mounted globally below
import SosHistoryPage from "./pages/sos/SosHistoryPage";
import SecurityDashboardPage from "./pages/admin/SecurityDashboardPage";

// Incident Reporting (Workflow 4)
import ReportIncidentPage from "./pages/incidents/ReportIncidentPage";
import MyIncidentsPage from "./pages/incidents/MyIncidentsPage";
import IncidentAdminPage from "./pages/incidents/IncidentAdminPage";

// Community (Workflow 5)
import CommunityFeed from "./pages/community/CommunityFeed";
import PostDetailPage from "./pages/community/PostDetailPage";

// Analytics + Admin Dashboard (Workflow 6)
import SafetyAnalyticsPage from "./pages/analytics/SafetyAnalyticsPage";
import AdminDashboardHome from "./pages/admin/AdminDashboardHome";
import UserManagementPage from "./pages/admin/UserManagementPage";
import AuditLogPage from "./pages/admin/AuditLogPage";

function AuthedLayout({ children }) {
  return (
    <div className="min-h-screen bg-gray-50">
      <NavBar />
      {children}
      {/* Mounted once, globally, for every logged-in user - see SosButton's own
          notes on why its location-ping loop is lifecycle-bound, not a background service. */}
      <SosButton />
    </div>
  );
}

function UnauthorizedPage() {
  return (
    <div className="max-w-md mx-auto py-20 px-4 text-center">
      <h1 className="text-lg font-bold text-gray-900 mb-2">Not authorized</h1>
      <p className="text-sm text-gray-500">Your account role doesn't have access to this page.</p>
    </div>
  );
}

function RootRedirect() {
  const { user, loading } = useAuth();
  if (loading) return null;
  return <Navigate to={user ? "/community" : "/login"} replace />;
}

export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          {/* Public */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          {/* Public, token-based - a trusted contact opens this with no account */}
          <Route path="/journey/track/:token" element={<TrackJourneyPage />} />
          <Route path="/unauthorized" element={<UnauthorizedPage />} />

          {/* Any authenticated user */}
          <Route path="/community" element={
            <ProtectedRoute><AuthedLayout><CommunityFeed /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/community/posts/:postId" element={
            <ProtectedRoute><AuthedLayout><PostDetailPage /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/journey" element={
            <ProtectedRoute><AuthedLayout><JourneyModePage /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/sos/history" element={
            <ProtectedRoute><AuthedLayout><SosHistoryPage /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/incidents/report" element={
            <ProtectedRoute><AuthedLayout><ReportIncidentPage /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/incidents/mine" element={
            <ProtectedRoute><AuthedLayout><MyIncidentsPage /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/contacts" element={
            <ProtectedRoute><AuthedLayout><TrustedContactsPage /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/profile" element={
            <ProtectedRoute><AuthedLayout><ProfilePage /></AuthedLayout></ProtectedRoute>
          } />

          {/* MODERATOR + ADMIN */}
          <Route path="/admin/dashboard" element={
            <ProtectedRoute roles={["MODERATOR", "ADMIN"]}><AuthedLayout><AdminDashboardHome /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/admin/incidents" element={
            <ProtectedRoute roles={["MODERATOR", "ADMIN"]}><AuthedLayout><IncidentAdminPage /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/admin/analytics" element={
            <ProtectedRoute roles={["MODERATOR", "ADMIN"]}><AuthedLayout><SafetyAnalyticsPage /></AuthedLayout></ProtectedRoute>
          } />

          {/* SECURITY_STAFF + ADMIN */}
          <Route path="/admin/security" element={
            <ProtectedRoute roles={["SECURITY_STAFF", "ADMIN"]}><AuthedLayout><SecurityDashboardPage /></AuthedLayout></ProtectedRoute>
          } />

          {/* ADMIN only */}
          <Route path="/admin/users" element={
            <ProtectedRoute roles={["ADMIN"]}><AuthedLayout><UserManagementPage /></AuthedLayout></ProtectedRoute>
          } />
          <Route path="/admin/audit-logs" element={
            <ProtectedRoute roles={["ADMIN"]}><AuthedLayout><AuditLogPage /></AuthedLayout></ProtectedRoute>
          } />

          <Route path="/" element={<RootRedirect />} />
          <Route path="*" element={<RootRedirect />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  );
}
