import { Route, Routes } from 'react-router-dom';
import { Header } from './components/Header';
import { DashboardPage } from './pages/DashboardPage';
import { QuestionListPage } from './pages/QuestionListPage';
import { QuestionSolvePage } from './pages/QuestionSolvePage';
import { FeedbackResultPage } from './pages/FeedbackResultPage';
import { InterviewStartPage } from './pages/InterviewStartPage';
import { MockInterviewPage } from './pages/MockInterviewPage';

function App() {
  return (
    <div className="app-shell">
      <Header />
      <Routes>
        <Route path="/" element={<DashboardPage />} />
        <Route path="/questions" element={<QuestionListPage />} />
        <Route path="/questions/:id" element={<QuestionSolvePage />} />
        <Route path="/answers/:answerId/feedback" element={<FeedbackResultPage />} />
        <Route path="/interviews" element={<InterviewStartPage />} />
        <Route path="/interviews/:id" element={<MockInterviewPage />} />
      </Routes>
    </div>
  );
}

export default App;
