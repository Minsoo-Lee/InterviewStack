// docs/openapi.yaml 스키마와 대응. 백엔드 계약이 바뀌면 함께 갱신할 것.

export type Category = 'CS 기초' | '백엔드 심화' | 'AI/ML' | '인성/직무';
export type Difficulty = '하' | '중' | '상';

export interface UserSummary {
  id: string;
  email: string;
  name: string;
}

export interface Question {
  id: string;
  category: Category;
  subTopic: string;
  difficulty: Difficulty;
  content: string;
}

export interface Answer {
  id: string;
  questionId: string;
  content: string;
  createdAt: string;
}

export interface ReferenceDocument {
  id: string;
  title: string;
  sourceUrl?: string | null;
}

export interface Feedback {
  id: string;
  rubricVersion: string;
  scores: Record<string, number>;
  total: number;
  summary: string;
  references: ReferenceDocument[];
  isWeak: boolean;
}

export interface AnswerWithFeedback {
  answer: Answer;
  feedback: Feedback;
}

export type InterviewMessageRole = 'ai' | 'user';

export interface InterviewMessage {
  id: string;
  role: InterviewMessageRole;
  content: string;
  createdAt: string;
}

export type InterviewStatus = 'in_progress' | 'completed';

export interface InterviewSession {
  id: string;
  status: InterviewStatus;
  startedAt: string;
  firstMessage: InterviewMessage;
}

export interface InterviewSessionDetail {
  id: string;
  status: InterviewStatus;
  startedAt: string;
  endedAt: string | null;
  topicsCovered: string[];
  messages: InterviewMessage[];
  report: string | null;
}

export interface DashboardCategorySummary {
  category: Category;
  averageScore: number;
  answeredCount: number;
}

export interface DashboardSummary {
  weakCategories: DashboardCategorySummary[];
  wrongAnswerNotes: AnswerWithFeedback[];
}
