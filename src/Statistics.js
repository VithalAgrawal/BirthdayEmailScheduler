import React, { useEffect, useState } from "react";
import axios from "axios";
import { Link } from "react-router-dom";

const Statistics = () => {
  const [logs, setLogs] = useState([]);
  const [filter, setFilter] = useState("All");

  useEffect(() => {
    // Fetch email logs from backend
    axios
      .get("http://localhost:7373/api/admin/getEmailLogs")
      .then((response) => setLogs(response.data))
      .catch((error) => console.log(error));
  }, []);

  const filteredLogs = logs.filter((log) => {
    if (filter === "All") return true;
    return log.status.toLowerCase() === filter.toLowerCase();
  });

  return (
    <>
      {/* Navigation Bar */}
      <nav className="navbar">
        <div className="navbar-container">
          <Link to="/" className="nav-link">
            Dashboard
          </Link>
          <Link to="/email-editor" className="nav-link">
            Email Template
          </Link>
          <Link to="/statistics" className="nav-link active">
            Statistics
          </Link>
        </div>
      </nav>

      {/* Main Content */}
      <div className="statistics-container">
        <h1>Email Log Viewer</h1>

        {/* Dropdown Filter */}
        <div className="filter-section">
          <label htmlFor="status-filter">Filter by Status:</label>
          <select
            id="status-filter"
            onChange={(e) => setFilter(e.target.value)}
            value={filter}
          >
            <option value="All">All</option>
            <option value="success">Success</option>
            <option value="failure">Failure</option>
          </select>
        </div>

        {/* Email Log Table */}
        <div className="table-section">
          <table className="statistics-table">
            <thead>
              <tr>
                <th>Date</th>
                <th>Employee ID</th>
                <th>Recipient</th>
                <th>Status</th>
                <th>Error (if any)</th>
              </tr>
            </thead>
            <tbody>
              {filteredLogs.length > 0 ? (
                filteredLogs.map((log, index) => (
                  <tr key={index}>
                    <td>{new Date(log.timestamp).toLocaleString()}</td>
                    <td>{log.employeeId || "N/A"}</td>
                    <td>{log.emailId}</td>
                    <td>
                      <span
                        className={`badge ${
                          log.status.toLowerCase() === "success"
                            ? "bg-success"
                            : "bg-danger"
                        }`}
                      >
                        {log.status}
                      </span>
                    </td>
                    <td>{log.error || "N/A"}</td>
                  </tr>
                ))
              ) : (
                <tr>
                  <td colSpan="5" className="no-data">
                    No logs available.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        </div>
      </div>
    </>
  );
};

export default Statistics;
