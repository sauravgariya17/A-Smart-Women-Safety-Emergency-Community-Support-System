import React from "react";
import { User, LogOut } from "lucide-react";
import { useAuth } from "../../context/AuthContext";
import { useNavigate } from "react-router-dom";

export default function ProfilePage() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const handleLogout = async () => {
    await logout();
    navigate("/login");
  };

  return (
    <div className="max-w-md mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-4">
        <User className="text-purple-600" size={22} />
        <h1 className="text-lg font-bold text-gray-900">My Profile</h1>
      </div>

      <div className="bg-white border border-gray-200 rounded-lg p-4 space-y-2 text-sm">
        <div className="flex justify-between"><span className="text-gray-500">Name</span><span className="font-medium">{user.name}</span></div>
        <div className="flex justify-between"><span className="text-gray-500">Email</span><span className="font-medium">{user.email}</span></div>
        <div className="flex justify-between">
          <span className="text-gray-500">Community handle</span>
          <span className="font-medium">u/{user.anonymousHandle}</span>
        </div>
        <div className="flex justify-between"><span className="text-gray-500">Role</span><span className="font-medium">{user.role}</span></div>
        <p className="text-xs text-gray-400 pt-2 border-t border-gray-100">
          Your name is never shown on community posts or comments - only your handle above is public there.
        </p>
      </div>

      <button
        onClick={handleLogout}
        className="w-full mt-4 flex items-center justify-center gap-2 py-2.5 rounded-md border border-gray-300 text-gray-600 hover:bg-gray-50"
      >
        <LogOut size={16} /> Log out
      </button>
    </div>
  );
}
