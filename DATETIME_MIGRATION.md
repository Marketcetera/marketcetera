# Java Date/Time API Migration Plan

## Overview

This document outlines the plan for migrating the Marketcetera codebase from legacy date/time APIs to the modern java.time API introduced in Java 8. This migration is important for:

1. Improved thread safety (java.util.Date and SimpleDateFormat are not thread-safe)
2. Better timezone handling
3. Improved API design with immutable objects
4. Java 11+ compatibility 
5. Removal of Joda-Time dependency

## Current Status

- Created a new `DateTimeUtils` class that uses java.time APIs
- Added `@Deprecated` annotations to `DateUtils` methods with references to the new `DateTimeUtils` class
- Updated MarketDataRpcUtilTest to use dynamic date generation for option expiry tests
- Modified OptionUtils.getFullYear() to handle years more intelligently (30-year sliding window)

## Migration Phases

### Phase 1: Preparation (COMPLETED)

- ✅ Create DateTimeUtils class with java.time equivalents for DateUtils functionality
- ✅ Add @Deprecated annotations to DateUtils methods
- ✅ Fix DateUtilsTest for Java 11 compatibility 
- ✅ Make tests more robust with dynamic date generation

### Phase 2: Direct Dependencies (NEXT)

Update the following classes that directly use DateUtils to use DateTimeUtils instead:

- [ ] SimulatedExchange
- [ ] OrderHistoryManager
- [ ] TopOfBookEventImpl
- [ ] LogEventImpl
- [ ] DepthOfBookEventImpl
- [ ] ApplicationVersion

### Phase 3: Core Date/Time Usage

Identify and update key areas where raw java.util.Date, Calendar, and SimpleDateFormat are used:

- [ ] Create adapter methods to bridge between Date and java.time types
- [ ] Replace direct SimpleDateFormat usage with DateTimeFormatter
- [ ] Add java.time equivalents for Calendar operations
- [ ] Update RPC layer date conversions (BaseRpcUtil)

### Phase 4: JodaTime Replacement

- [ ] Create conversion methods for JodaTime to java.time types
- [ ] Replace Joda DateTime with java.time ZonedDateTime
- [ ] Replace Joda LocalDate with java.time LocalDate
- [ ] Fully remove JodaTime dependency once all usages are eliminated

## Guidelines for Migration

When performing the migration, follow these principles:

1. **Immutability**: java.time classes are immutable, maintain this design principle
2. **Thread safety**: Avoid shared DateTimeFormatter instances that aren't thread-safe
3. **Clear type usage**: Prefer:
   - Instant for timestamps without timezone info
   - ZonedDateTime for timestamps with timezone
   - LocalDateTime for date/time without timezone
   - LocalDate for dates without time components
4. **Timezone handling**: Explicitly specify timezone (usually UTC) when converting

## Testing Strategy

1. Ensure all existing tests pass after each phase of migration
2. Add specific tests for date/time edge cases, particularly around:
   - DST transitions
   - Different timezones
   - Date parsing formats
   - Date/time calculations

## Migration Completion Criteria

- All usages of java.util.Date, Calendar, and SimpleDateFormat are replaced
- Joda-Time dependency is removed
- DateUtils class is fully deprecated and minimally used
- All tests pass in Java 11+