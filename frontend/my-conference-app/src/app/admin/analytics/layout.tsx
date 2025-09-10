import { AuthGuard } from "@/components/auth/auth-guard";

export default function AnalyticsLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return <AuthGuard requiredRole="ADMIN">{children}</AuthGuard>;
}
