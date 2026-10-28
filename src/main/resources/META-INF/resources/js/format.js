/**
 * Standard navigational coordinate formatter for Shoe Tracker.
 * Formats decimal degrees to: N XX° MM,MMM' / E XXX° MM,MMM'
 */
function formatCoordinate(coord, isLat) {
  if (coord === null || coord === undefined || isNaN(coord)) {
    return isLat ? "N 00° 00,000'" : "E 000° 00,000'";
  }
  const dir = isLat ? (coord >= 0 ? 'N' : 'S') : (coord >= 0 ? 'E' : 'W');
  const abs = Math.abs(coord);
  let degrees = Math.floor(abs);
  let minutes = (abs - degrees) * 60;
  let minFixed = minutes.toFixed(3);

  // Handle rounding rollover when minutes round up to 60.000
  if (minFixed === "60.000" || minFixed === "60") {
    degrees += 1;
    minFixed = "00.000";
  }

  const degStr = isLat ? String(degrees).padStart(2, '0') : String(degrees).padStart(3, '0');
  const minPadded = minFixed.indexOf('.') === 1 ? '0' + minFixed : minFixed;
  const minStr = minPadded.replace('.', ',');
  return `${dir} ${degStr}° ${minStr}'`;
}

function formatLocation(lat, lon) {
  return `${formatCoordinate(lat, true)} / ${formatCoordinate(lon, false)}`;
}

function formatLocationMultiline(lat, lon) {
  return `${formatCoordinate(lat, true)}<br>${formatCoordinate(lon, false)}`;
}

// Expose globally for script tag access
window.ShoeTracker = window.ShoeTracker || {};
window.ShoeTracker.formatCoordinate = formatCoordinate;
window.ShoeTracker.formatLocation = formatLocation;
window.ShoeTracker.formatLocationMultiline = formatLocationMultiline;
