import React, { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ShieldCheck } from "lucide-react";
import { useAuth } from "../../context/AuthContext";

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [form, setForm] = useState({ name: "", email: "", password: "", phone: "" });
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  const update = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError("");
    setLoading(true);
    try {
      await register(form);
      navigate("/community");
    } catch (err) {
      const messages = err?.response?.data?.errors;
      setError(messages?.join(", ") || err?.response?.data?.message || "Could not register. Please try again.");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="max-w-sm mx-auto py-16 px-4">
      <div className="flex items-center gap-2 justify-center mb-6">
        <ShieldCheck className="text-purple-600" size={28} />
        <h1 className="text-lg font-bold text-gray-900">Create your account</h1>
      </div>

      <form onSubmit={handleSubmit} className="space-y-3">
        <input required placeholder="Full name" className="w-full border border-gray-300 rounded-md p-2.5 text-sm" value={form.name} onChange={update("name")} />
        <input required type="email" placeholder="Email" className="w-full border border-gray-300 rounded-md p-2.5 text-sm" value={form.email} onChange={update("email")} />
        <input required type="password" placeholder="Password (8+ chars, letter+number+symbol)" className="w-full border border-gray-300 rounded-md p-2.5 text-sm" value={form.password} onChange={update("password")} />
        <input placeholder="Phone (optional)" className="w-full border border-gray-300 rounded-md p-2.5 text-sm" value={form.phone} onChange={update("phone")} />
        {error && <p className="text-sm text-red-600">{error}</p>}
        <button
          type="submit"
          disabled={loading}
          className="w-full py-2.5 rounded-md bg-purple-600 text-white font-medium hover:bg-purple-700 disabled:opacity-50"
        >
          {loading ? "Creating account..." : "Create account"}
        </button>
      </form>

      <p className="text-sm text-gray-500 text-center mt-4">
        Already have an account? <Link to="/login" className="text-purple-600 hover:underline">Log in</Link>
      </p>
    </div>
  );
}
