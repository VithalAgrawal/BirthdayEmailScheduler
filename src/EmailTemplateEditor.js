import React, { useState, useEffect } from "react";
import axios from "axios";
import { Link } from "react-router-dom";
import "./EmailTemplateEditor.css";
import View from "./view-img.png";
import Close from "./close.png";



const EmailTemplateEditor = () => {
  const [message, setMessage] = useState("");
  const [imageUrl, setImageUrl] = useState("");
  const [showPreview, setShowPreview] = useState(false);
  const [templates, setTemplates] = useState([]); // List of templates
  const [searchQuery, setSearchQuery] = useState(""); // Search bar input
  const [filteredTemplates, setFilteredTemplates] = useState([]); // Filtered results
  const [selectedTemplate, setSelectedTemplate] = useState(null); // Selected template for update
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [templatesCard, setTemplatesCard] = useState([]);
  const [selectedTemplatePre, setSelectedTemplatePre] = useState(null);
  const [messagePre, setMessagePre] = useState("");
  const [imageUrlPre, setImageUrlPre] = useState("");
  const [selectedTemplateIdPre, setSelectedTemplateIdPre] = useState(1);

  // Fetch all templates
  useEffect(() => {
    axios
      .get("http://localhost:7373/api/admin/getAllTemplates")
      .then((response) => {
        setTemplates(response.data);
        setTemplatesCard(response.data);
        setFilteredTemplates(response.data);
      })
      .catch((error) => console.error("Error fetching templates:", error));
  }, []);

  const handlePreview = () => {
    setShowPreview(true);
  };

  const handleModalOpen = () => {
    setIsModalOpen(true);
  }
  const handleModalClose = () => {
    setIsModalOpen(false);
  }
  
  const Card = ({temp} ) => {
    return(<div className="small-card" onClick={() => handleTemplateSelectPre(temp)}>
      <img src={temp.image_url} alt="template" width="100%" />
    </div>)
  }
  const handleSaveTemplate = async () => {
    try {
      const emailTemplate = {
        subject: "Happy Birthday User",
        message_body: message || "Wishing you a day filled with love and joy!",
        image_url: imageUrl || "",
      };

      const response = await axios.post(
        "http://localhost:7373/api/admin/saveTemplate",
        emailTemplate,
        { headers: { "Content-Type": "application/json" } }
      );

      if (response.status === 200) {
        alert("Email Template saved successfully!");
        const updatedTemplates = await axios.get(
          "http://localhost:7373/api/admin/getAllTemplates"
        );
        setTemplates(updatedTemplates.data);
        setFilteredTemplates(updatedTemplates.data);
        setTemplatesCard(updatedTemplates.data);
      }
    } catch (error) {
      console.error("Error saving email template:", error.response?.data || error);
      alert("Failed to save email template. Please check your input.");
    }
  };

  const handleUpdateTemplate = async () => {
    if (!selectedTemplate) {
      alert("Please select a template to update.");
      return;
    }

    const updatedTemplate = {
      subject: "HBD",
      message_body: message,
      image_url: imageUrl,
    };

    try {
      const response = await axios.put(
        `http://localhost:7373/api/admin/updateTemplate/${selectedTemplate.id}`,
        updatedTemplate,
        { headers: { "Content-Type": "application/json" } }
      );

      if (response.status === 200) {
        alert("Template updated successfully!");
        const refreshedTemplates = await axios.get(
          "http://localhost:7373/api/admin/getAllTemplates"
        );
        setTemplates(refreshedTemplates.data);
        setFilteredTemplates(refreshedTemplates.data);
      }
    } catch (error) {
      console.error("Error updating template:", error.response?.data || error);
      alert("Failed to update template. Please check your input.");
    }
  };

  const handleSearch = (e) => {
    const query = e.target.value;
    setSearchQuery(query);

    const filtered = templates.filter((template) =>
      template.id.toString().includes(query)
    );
    setFilteredTemplates(filtered);
  };

  const handleTemplateSelect = (template) => {
    setSelectedTemplate(template);
    setMessage(template.message_body);
    setImageUrl(template.image_url);

    // Close the dropdown by clearing the search query
    setSearchQuery("");
  };
  const handleTemplateSelectPre = (template) => {
    setSelectedTemplatePre(template);
    setMessagePre(template.message_body);
    setImageUrlPre(template.image_url);
    setSelectedTemplateIdPre(template.id);
  };

  return (
    <div className="main">
      <nav className="navbar">
        <div className="navbar-container">
          <Link to="/" className="nav-link nav-links">
            Dashboard
          </Link>
          <Link to="/email-editor" className="nav-link active">
            Email Template
          </Link>
          <Link to="/statistics" className="nav-link">
            Statistics
          </Link>
        </div>
      </nav>

      <div className="container">
        <h1>Birthday Email Editor</h1>

        <div className="form-group">
          <label htmlFor="search-bar">Search Template:</label>
          <div className="searchid">
          <input
            type="text"
            id="search-bar"
            placeholder="Search by ID"
            value={searchQuery}
            onChange={handleSearch}
            className="form-control"
          />
          <button className="view-btn"
            onClick={handleModalOpen}
          >
            <img src={View} alt="" height="24px" width="24px" />
          </button>
          </div>
          
        </div>

        {searchQuery && (
          <div className="search-results">
            {filteredTemplates.length > 0 ? (
              <ul>
                {filteredTemplates.map((template) => (
                  <li
                    key={template.id}
                    onClick={() => handleTemplateSelect(template)}
                    className={`search-result-item ${
                      selectedTemplate && selectedTemplate.id === template.id ? "selected" : ""
                    }`}
                  >
                    <strong>ID:</strong> {template.id}
                  </li>
                ))}
              </ul>
            ) : (
              <p className="no-results">No templates found.</p>
            )}
          </div>
        )}

        <form className="editor-form">
          <div className="form-group">
            <label htmlFor="message">Birthday Message:</label>
            <textarea
              id="message"
              rows="5"
              placeholder="Write your birthday message here"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
            ></textarea>
          </div>

          <div className="form-group">
            <label htmlFor="image-url">Image URL :</label>
            <input
              type="text"
              id="image-url"
              placeholder="Paste an image URL here"
              value={imageUrl}
              onChange={(e) => setImageUrl(e.target.value)}
            />
          </div>

          <div className="button-container">
            <button
              type="button"
              id="preview-button"
              className="preview-button"
              onClick={handlePreview}
            >
              Preview Email
            </button>
            <button
              type="button"
              className="save-button"
              onClick={selectedTemplate ? handleUpdateTemplate : handleSaveTemplate}
            >
              {selectedTemplate ? "Update Template" : "Save Template"}
            </button>
          </div>
        </form>

        {showPreview && (
          <div id="email-preview" className="email-template">
            <div className="email-header">
              <h2>
                Happy Birthday <span id="preview-name">User</span>!
              </h2>
            </div>
            <div className="email-content">
              <p id="preview-message">
                {message || "Wishing you a day filled with love and joy!"}
              </p>
              {imageUrl && (
                <div id="preview-image">
                  <img src={imageUrl} alt="Birthday Image" />
                </div>
              )}
            </div>
            <div className="email-footer">
              <p>Warm wishes,</p>
              <p>ABC Group</p>
            </div>
          </div>
        )}
      </div>
      {isModalOpen && (
        <div className="modal">
          <div className="modal-main">
            <div className="modal-left">
            {templatesCard.map((template) => (
              <Card 
                key={template.id} 
                temp={template} 
                 
              />))}

            </div>
            <div className="modal-right"> 
              <div className="pre-id">
                ID : {selectedTemplateIdPre}
              </div>
                <div>
                <div id="email-preview" className="email-template">
            <div className="email-header">
              <h2>
                Happy Birthday <span id="preview-name">User</span>!
              </h2>
            </div>
            <div className="email-content">
              <p id="preview-message">
                {messagePre || "Wishing you a day filled with love and joy!"}
              </p>
              {imageUrlPre && (
                <div id="preview-image">
                  <img src={imageUrlPre} alt="Birthday Image" />
                </div>
              )}
            </div>
            <div className="email-footer">
              <p>Warm wishes,</p>
              <p>ABC Group</p>
            </div>
          </div>
                </div>
            </div>
          </div>
          <button className="close-btn" onClick={handleModalClose}>
            <img src={Close} alt="" height="24px" width="24px" />
          </button>
        </div>
      )}
    </div>
  );
};

export default EmailTemplateEditor;
