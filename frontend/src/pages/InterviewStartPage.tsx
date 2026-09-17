import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { apiClient } from '../api/client';
import type { InterviewSession } from '../types';

// TODO: POST /api/interviews 연동
export function InterviewStartPage() {
  const navigate = useNavigate();
  const [resumeText, setResumeText] = useState('');
  const [jdText, setJdText] = useState('');
  const [starting, setStarting] = useState(false);

  async function handleStart() {
    setStarting(true);
    try {
      const res = await apiClient.post<InterviewSession>('/api/interviews', {
        resumeText: resumeText || null,
        jdText: jdText || null,
      });
      navigate(`/interviews/${res.data.id}`);
    } finally {
      setStarting(false);
    }
  }

  return (
    <div className="page">
      <h1>AI 모의면접 시작</h1>
      <p className="page__hint">이력서/JD를 입력하면 맞춤 질문으로 세션을 시작합니다. (선택 입력)</p>

      <textarea
        rows={6}
        placeholder="이력서 내용 붙여넣기 (선택)"
        value={resumeText}
        onChange={(e) => setResumeText(e.target.value)}
      />
      <textarea
        rows={4}
        placeholder="채용공고(JD) 내용 붙여넣기 (선택)"
        value={jdText}
        onChange={(e) => setJdText(e.target.value)}
      />

      <button type="button" disabled={starting} onClick={handleStart}>
        {starting ? '세션 시작 중...' : '모의면접 시작'}
      </button>
    </div>
  );
}
