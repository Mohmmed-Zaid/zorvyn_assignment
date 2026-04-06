package com.finance.demo.security;



@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
    @NonNull
    HttpServletRequest request,
    @NonNull
    HttpServletResponse response,
    @NonNull
    FilterChain filterChain) throws
            ServletException, IOException {

        String token = extractToken(request);

        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                String email = jwtUtils.extractEmail(token);
                UserDetails userDetails = userDetailsService.loadUserByUsername(email);

                if (jwtUtils.validateToken(token, userDetails)) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            } catch (Exception e) {

            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(class="str">"Authorization"
);
        if (StringUtils.hasText(header) && header.startsWith(class="str">"Bearer "
)) {
            return header.substring(7);
        }
        return null;
    }
}