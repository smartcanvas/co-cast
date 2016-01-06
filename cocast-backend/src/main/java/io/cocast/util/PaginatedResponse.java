package io.cocast.util;

import java.util.List;

/**
 * Responses for paginated requests
 */
public class PaginatedResponse {


    private Meta meta;
    private List data;

    public PaginatedResponse(List data, Integer offset, Integer total) {
        meta = new Meta(data, offset, total);
        this.data = data;
    }

    public Meta getMeta() {
        return meta;
    }

    public void setMeta(Meta meta) {
        this.meta = meta;
    }

    public List getData() {
        return data;
    }

    public void setData(List data) {
        this.data = data;
    }

    private class Meta {
        private Integer total;
        private Integer itensInPage;
        private Integer offset;

        public Meta(List data, Integer offset, Integer total) {
            this.itensInPage = data.size();
            this.offset = offset;
            this.total = total;
        }

        public Integer getTotal() {
            return total;
        }

        public void setTotal(Integer total) {
            this.total = total;
        }

        public Integer getItensInPage() {
            return itensInPage;
        }

        public void setItensInPage(Integer itensInPage) {
            this.itensInPage = itensInPage;
        }

        public Integer getOffset() {
            return offset;
        }

        public void setOffset(Integer offset) {
            this.offset = offset;
        }
    }

}
