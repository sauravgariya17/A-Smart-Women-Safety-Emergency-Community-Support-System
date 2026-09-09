# GEHU Women Safety - Frontend (Community Module)

This session delivers the **Community feature's** React components only
(not the full app shell - that will come with the Auth/Profile frontend
in a later session). To run these standalone, wire them into a Vite +
React Router + Tailwind project:

1. `npm create vite@latest . -- --template react`
2. `npm install` then `npm install react-router-dom axios lucide-react`
3. `npm install -D tailwindcss postcss autoprefixer` and run `npx tailwindcss init -p`
4. Set `VITE_API_BASE_URL=http://localhost:8080/api` in a `.env` file
5. Add a route: `<Route path="/community" element={<CommunityFeed />} />`
   and `<Route path="/community/posts/:postId" element={<PostDetailPage />} />`
6. Ensure `accessToken`/`refreshToken` are already in `localStorage` from
   your Auth flow (built in Stage 1) before visiting `/community`.

## Files in this module
- `src/api/axiosInstance.js` - shared JWT-aware HTTP client
- `src/api/communityApi.js` - community endpoint wrappers + category list
- `src/pages/community/CommunityFeed.jsx` - main feed: search/filter/sort/paginate/create
- `src/pages/community/PostDetailPage.jsx` - full post + comments
- `src/pages/community/components/PostCard.jsx`
- `src/pages/community/components/CommentList.jsx`
- `src/pages/community/components/VoteButtons.jsx`
- `src/pages/community/components/CreatePostForm.jsx`
- `src/pages/community/components/ReportModal.jsx`
- `src/pages/community/components/CategoryBadge.jsx`

## Privacy note baked into the UI
Every place a post/comment author would normally show a name instead shows
`u/{anonymousHandle}` — the components don't even receive a user ID or name
in their props, because the backend DTOs never send one.
