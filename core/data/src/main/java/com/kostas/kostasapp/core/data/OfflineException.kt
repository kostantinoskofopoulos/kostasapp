package com.kostas.kostasapp.core.data

import java.io.IOException

/**
 * Used to distinguish "expected offline" failures from real server bugs.
 * Paging UI can show a friendly cached-mode footer with a Retry button.
 */
class OfflineException : IOException("Offline")