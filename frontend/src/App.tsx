import { Route, Routes } from 'react-router-dom';
import { Header } from './components/Header';
import { DashboardPage } from './pages/DashboardPage';
import { QuestionListPage } from './pages/QuestionListPage';
import { QuestionSolvePage } from './pages/QuestionSolvePage';
import { FeedbackResultPage } from './pages/FeedbackResultPage';
import { InterviewStartPage } from './pages/InterviewStartPage';
import { MockInterviewPage } from './pages/MockInterviewPage';
import { LoginPage } from './pages/LoginPage';
import { SignupPage } from './pages/SignupPage';
import { MyPage } from './pages/MyPage';
import { RequireAuth } from './auth/RequireAuth';

function App() {
  return (
    <div className="app-shell">
      <Header />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />

        <Route
          path="/"
          element={
            <RequireAuth>
              <DashboardPage />
            </RequireAuth>
          }
        />
        <Route
          path="/questions"
          element={
            <RequireAuth>
              <QuestionListPage />
            </RequireAuth>
          }
        />
        <Route
          path="/questions/:id"
          element={
            <RequireAuth>
              <QuestionSolvePage />
            </RequireAuth>
          }
        />
        <Route
          path="/answers/:answerId/feedback"
          element={
            <RequireAuth>
              <FeedbackResultPage />
            </RequireAuth>
          }
        />
        <Route
          path="/interviews"
          element={
            <RequireAuth>
              <InterviewStartPage />
            </RequireAuth>
          }
        />
        <Route
          path="/interviews/:id"
          element={
            <RequireAuth>
              <MockInterviewPage />
            </RequireAuth>
          }
        />
        <Route
          path="/me"
          element={
            <RequireAuth>
              <MyPage />
            </RequireAuth>
          }
        />
      </Routes>
    </div>
  );
}

export default App;
