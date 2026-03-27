import fs from 'node:fs';
import path from 'node:path';

const variant = process.argv[2];
const allowedVariants = new Set(['developmentLibrary', 'productionLibrary']);

if (!allowedVariants.has(variant)) {
  console.error(
    `Invalid shared JS variant "${variant}". Use one of: ${Array.from(allowedVariants).join(', ')}.`
  );
  process.exit(1);
}

const rootDir = process.cwd();
const packageJsonPath = path.join(rootDir, 'package.json');
const packageLockPath = path.join(rootDir, 'package-lock.json');
const workspacePath = `shared/build/dist/js/${variant}`;

function updateWorkspaceFile(filePath) {
  if (!fs.existsSync(filePath)) return;

  const document = JSON.parse(fs.readFileSync(filePath, 'utf8'));

  if (Array.isArray(document.workspaces)) {
    document.workspaces = document.workspaces.map((entry) =>
      entry.startsWith('shared/build/dist/js/') ? workspacePath : entry
    );
  }

  if (document.packages?.[''] && Array.isArray(document.packages[''].workspaces)) {
    document.packages[''].workspaces = document.packages[''].workspaces.map((entry) =>
      entry.startsWith('shared/build/dist/js/') ? workspacePath : entry
    );
  }

  fs.writeFileSync(filePath, `${JSON.stringify(document, null, 2)}\n`);
}

updateWorkspaceFile(packageJsonPath);
updateWorkspaceFile(packageLockPath);

console.log(`Configured shared workspace to ${workspacePath}`);
