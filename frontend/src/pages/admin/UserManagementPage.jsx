import React, { useEffect, useState } from "react";
import { Users, Search } from "lucide-react";
import { UserAdminApi, ROLES } from "../../api/adminApi";

export default function UserManagementPage() {
  const [users, setUsers] = useState([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [role, setRole] = useState("");
  const [keyword, setKeyword] = useState("");
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  const load = (targetPage = 0) => {
    setLoading(true);
    UserAdminApi.listUsers({ role: role || undefined, keyword: keyword || undefined, page: targetPage, size: 15 })
      .then((data) => {
        setUsers(data.content);
        setPage(data.page);
        setTotalPages(data.totalPages);
      })
      .finally(() => setLoading(false));
  };

  useEffect(() => {
    load(0);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [role]);

  const handleSearch = (e) => {
    e.preventDefault();
    load(0);
  };

  const changeRole = async (userId, newRole) => {
    setError("");
    try {
      await UserAdminApi.updateRole(userId, newRole);
      load(page);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not update role.");
    }
  };

  const toggleStatus = async (userId, active) => {
    setError("");
    try {
      await UserAdminApi.updateStatus(userId, active);
      load(page);
    } catch (err) {
      setError(err?.response?.data?.message || "Could not update status.");
    }
  };

  return (
    <div className="max-w-4xl mx-auto py-6 px-4">
      <div className="flex items-center gap-2 mb-4">
        <Users className="text-purple-600" size={24} />
        <h1 className="text-xl font-bold text-gray-900">User Management</h1>
      </div>

      <form onSubmit={handleSearch} className="flex flex-wrap gap-2 mb-4">
        <div className="flex-1 min-w-[180px] relative">
          <Search size={16} className="absolute left-2 top-2.5 text-gray-400" />
          <input
            className="w-full border border-gray-300 rounded-md pl-8 pr-2 py-2 text-sm"
            placeholder="Search by name or email..."
            value={keyword}
            onChange={(e) => setKeyword(e.target.value)}
          />
        </div>
        <select className="border border-gray-300 rounded-md px-2 py-2 text-sm" value={role} onChange={(e) => setRole(e.target.value)}>
          <option value="">All roles</option>
          {ROLES.map((r) => <option key={r} value={r}>{r}</option>)}
        </select>
        <button type="submit" className="px-3 py-2 text-sm rounded-md bg-purple-600 text-white hover:bg-purple-700">
          Search
        </button>
      </form>

      {error && <p className="text-sm text-red-600 mb-3">{error}</p>}

      {loading ? (
        <p className="text-sm text-gray-400">Loading...</p>
      ) : (
        <div className="overflow-x-auto bg-white border border-gray-200 rounded-lg">
          <table className="w-full text-sm">
            <thead className="bg-gray-50 text-gray-500 text-xs uppercase">
              <tr>
                <th className="text-left px-3 py-2">Name</th>
                <th className="text-left px-3 py-2">Email</th>
                <th className="text-left px-3 py-2">Role</th>
                <th className="text-left px-3 py-2">Status</th>
                <th className="text-left px-3 py-2">Joined</th>
                <th className="text-left px-3 py-2">Actions</th>
              </tr>
            </thead>
            <tbody>
              {users.map((u) => (
                <tr key={u.id} className="border-t border-gray-100">
                  <td className="px-3 py-2">{u.name}</td>
                  <td className="px-3 py-2 text-gray-500">{u.email}</td>
                  <td className="px-3 py-2">
                    <select
                      className="border border-gray-300 rounded px-1.5 py-1 text-xs"
                      value={u.role}
                      onChange={(e) => changeRole(u.id, e.target.value)}
                    >
                      {ROLES.map((r) => <option key={r} value={r}>{r}</option>)}
                    </select>
                  </td>
                  <td className="px-3 py-2">
                    <span className={`text-xs px-2 py-0.5 rounded-full ${u.active ? "bg-green-100 text-green-700" : "bg-red-100 text-red-700"}`}>
                      {u.active ? "Active" : "Suspended"}
                    </span>
                  </td>
                  <td className="px-3 py-2 text-gray-400">{new Date(u.createdAt).toLocaleDateString()}</td>
                  <td className="px-3 py-2">
                    <button
                      onClick={() => toggleStatus(u.id, !u.active)}
                      className={`text-xs px-2 py-1 rounded-md ${u.active ? "bg-red-50 text-red-600 hover:bg-red-100" : "bg-green-50 text-green-600 hover:bg-green-100"}`}
                    >
                      {u.active ? "Suspend" : "Activate"}
                    </button>
                  </td>
                </tr>
              ))}
              {users.length === 0 && (
                <tr><td colSpan={6} className="px-3 py-6 text-center text-gray-400">No users found.</td></tr>
              )}
            </tbody>
          </table>
        </div>
      )}

      {totalPages > 1 && (
        <div className="flex justify-center gap-2 mt-4">
          <button disabled={page === 0} onClick={() => load(page - 1)} className="px-3 py-1 text-sm rounded-md border border-gray-300 disabled:opacity-40">
            Previous
          </button>
          <span className="text-sm text-gray-500 py-1">Page {page + 1} of {totalPages}</span>
          <button disabled={page + 1 >= totalPages} onClick={() => load(page + 1)} className="px-3 py-1 text-sm rounded-md border border-gray-300 disabled:opacity-40">
            Next
          </button>
        </div>
      )}
    </div>
  );
}
