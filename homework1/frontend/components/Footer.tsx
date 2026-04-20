export default function Footer() {
  return (
    <footer className="border-t border-gray-100 bg-white mt-auto">
      <div className="max-w-6xl mx-auto px-6 py-8">
        <div className="flex flex-col sm:flex-row items-center justify-between gap-6">
          <div className="flex items-center gap-3">
            <div className="flex items-center justify-center w-9 h-9 rounded-xl bg-[#FF6000]">
              <span className="text-white font-extrabold text-base leading-none">n11</span>
            </div>
            <div>
              <p className="text-sm font-bold text-gray-800">N11 Backend Bootcamp</p>
              <p className="text-xs text-gray-400">Homework 1 — Payment System</p>
            </div>
          </div>

          <div className="text-center sm:text-right">
            <p className="text-sm font-semibold text-gray-700">Kadir Tuna</p>
            <p className="text-xs text-gray-400">Software Engineer</p>
          </div>
        </div>

        <div className="mt-6 pt-6 border-t border-gray-100 flex flex-col sm:flex-row items-center justify-between gap-2 text-xs text-gray-300">
          <p>Built with Spring Boot · Chain of Responsibility · Java Reflection · SOLID Principles</p>
          <p>© {new Date().getFullYear()} Kadir Tuna</p>
        </div>
      </div>
    </footer>
  );
}
