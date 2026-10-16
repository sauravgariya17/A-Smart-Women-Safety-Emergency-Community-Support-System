import React, { useEffect, useState } from "react";
import { Users, Plus, Trash2 } from "lucide-react";
import api from "../../api/axiosInstance";

export default function TrustedContactsPage() {
  const [contacts, setContacts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [showForm, setShowForm] = useState(false);
  const [form, setForm] = useState({ name: "", phone: "", email: "", relation: "" });
  const [error, setError] = useState("");

  const load = () => {
    setLoading(true);
    api.get("/contacts").then((res) => setContacts(res.data.data)).finally(() => setLoading(false));
  };

  useEffect(load, []);

  const update = (field) => (e) => setForm((f) => ({ ...f, [field]: e.target.value }));

  const handleAdd = async (e) => {
    e.preventDefault();
    setError("");
    try {
      await api.post("/contacts", form);
      setForm({ name: "", phone: "", email: "", relation: "" });
      setShowForm(false);
      load();
    } catch (err) {
      setError(err?.response?.data?.message || "Could not add contact.");
    }
  };

  const handleDelete = async (id) => {
    await api.delete(`/contacts/${id}`);
    load();
  };

  return (
    <div className="max-w-md mx-auto py-6 px-4">
      <div className="flex items-center justify-between mb-4">
        <div className="flex items-center gap-2">
          <Users className="text-purple-600" size={22} />
          <h1 className="text-lg font-bold text-gray-900">Trusted Contacts</h1>
        </div>
        <button onClick={() => setShowForm((s) => !s)} className="flex items-center gap-1 text-sm text-purple-600 hover:underline">
          <Plus size={14} /> Add
        </button>
      </div>
      <p className="text-xs text-gray-400 mb-4">
        These contacts are notified during an SOS and can follow your active Journey Mode link. Up to 5.
      </p>

      {showForm && (
        <form onSubmit={handleAdd} className="bg-white border border-gray-200 rounded-md p-3 space-y-2 mb-4">
          <input required placeholder="Name" className="w-full border border-gray-300 rounded-md p-2 text-sm" value={form.name} onChange={update("name")} />
          <input required placeholder="Phone" className="w-full border border-gray-300 rounded-md p-2 text-sm" value={form.phone} onChange={update("phone")} />
          <input type="email" placeholder="Email (needed to actually notify them)" className="w-full border border-gray-300 rounded-md p-2 text-sm" value={form.email} onChange={update("email")} />
          <input placeholder="Relation (e.g. Mother, Friend)" className="w-full border border-gray-300 rounded-md p-2 text-sm" value={form.relation} onChange={update("relation")} />
          {error && <p className="text-xs text-red-600">{error}</p>}
          <button type="submit" className="w-full py-2 rounded-md bg-purple-600 text-white text-sm font-medium hover:bg-purple-700">
            Save contact
          </button>
        </form>
      )}

      {loading ? (
        <p className="text-sm text-gray-400">Loading...</p>
      ) : (
        <div className="space-y-2">
          {contacts.map((c) => (
            <div key={c.id} className="bg-white border border-gray-200 rounded-md p-3 flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-800">{c.name} <span className="text-xs text-gray-400">({c.relation})</span></p>
                <p className="text-xs text-gray-500">{c.phone} {c.email && `· ${c.email}`}</p>
              </div>
              <button onClick={() => handleDelete(c.id)} className="text-gray-400 hover:text-red-600">
                <Trash2 size={16} />
              </button>
            </div>
          ))}
          {contacts.length === 0 && <p className="text-sm text-gray-400">No trusted contacts added yet.</p>}
        </div>
      )}
    </div>
  );
}
