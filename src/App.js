import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Statistics from "./Statistics";
import Dashboard from "./Dashboard";
import EmailTemplateEditor from "./EmailTemplateEditor";

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<Dashboard />} />
        <Route path="/email-editor" element={<EmailTemplateEditor />} />
        <Route path="/statistics" element={<Statistics />} />
      </Routes>
    </Router>
  );
}

export default App;
