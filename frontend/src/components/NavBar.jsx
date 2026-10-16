import React from "react";
import { Link, useNavigate } from "react-router-dom";
import { ShieldCheck, User } from "lucide-react";
import { useAuth } from "../context/AuthContext";

export default function NavBar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const isStaff = ["MODERATOR", "ADMIN"].includes(user.role);
  const isAdmin = user.role === "ADMIN";
  const isSecurity = ["SECURITY_STAFF", "ADMIN"].includes(user.role);

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  return (
    <nav className="bg-white border-b border-gray-200 px-4 py-2.5 flex items-center gap-4 overflow-x-auto">
      <Link to="/community" className="flex items-center gap-1.5 font-bold text-purple-700 shrink-0">
        <ShieldCheck size={18} /> GEHU Safety
      </Link>
      <Link to="/community" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Community</Link>
      <Link to="/journey" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Journey Mode</Link>
      <Link to="/incidents/report" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Report Incident</Link>
      <Link to="/incidents/mine" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">My Reports</Link>
      <Link to="/contacts" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Trusted Contacts</Link>

      {isStaff && (
        <>
          <span className="text-gray-300">|</span>
          <Link to="/admin/dashboard" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Dashboard</Link>
          <Link to="/admin/incidents" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Incident Queue</Link>
          <Link to="/admin/analytics" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Analytics</Link>
        </>
      )}
      {isSecurity && (
        <Link to="/admin/security" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">SOS Queue</Link>
      )}
      {isAdmin && (
        <>
          <Link to="/admin/users" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Users</Link>
          <Link to="/admin/audit-logs" className="text-sm text-gray-600 hover:text-purple-700 whitespace-nowrap">Audit Log</Link>
        </>
      )}

      <div className="ml-auto flex items-center gap-3 shrink-0">
        <Link to="/profile" className="flex items-center gap-1 text-sm text-gray-600 hover:text-purple-700">
          <User size={14} /> u/{user.anonymousHandle}
        </Link>
        <button onClick={handleLogout} className="text-sm text-gray-400 hover:text-red-600">Log out</button>
      </div>
    </nav>
  );
}
