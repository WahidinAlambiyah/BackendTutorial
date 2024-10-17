<template>
  <div class="row justify-content-center">
    <div class="col-8">
      <form name="editForm" novalidate v-on:submit.prevent="save()">
        <h2
          id="monolithApp.trxProductHistory.home.createOrEditLabel"
          data-cy="TrxProductHistoryCreateUpdateHeading"
          v-text="t$('monolithApp.trxProductHistory.home.createOrEditLabel')"
        ></h2>
        <div>
          <div class="form-group" v-if="trxProductHistory.id">
            <label for="id" v-text="t$('global.field.id')"></label>
            <input type="text" class="form-control" id="id" name="id" v-model="trxProductHistory.id" readonly />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxProductHistory.oldPrice')"
              for="trx-product-history-oldPrice"
            ></label>
            <input
              type="number"
              class="form-control"
              name="oldPrice"
              id="trx-product-history-oldPrice"
              data-cy="oldPrice"
              :class="{ valid: !v$.oldPrice.$invalid, invalid: v$.oldPrice.$invalid }"
              v-model.number="v$.oldPrice.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxProductHistory.newPrice')"
              for="trx-product-history-newPrice"
            ></label>
            <input
              type="number"
              class="form-control"
              name="newPrice"
              id="trx-product-history-newPrice"
              data-cy="newPrice"
              :class="{ valid: !v$.newPrice.$invalid, invalid: v$.newPrice.$invalid }"
              v-model.number="v$.newPrice.$model"
            />
          </div>
          <div class="form-group">
            <label
              class="form-control-label"
              v-text="t$('monolithApp.trxProductHistory.changeDate')"
              for="trx-product-history-changeDate"
            ></label>
            <div class="d-flex">
              <input
                id="trx-product-history-changeDate"
                data-cy="changeDate"
                type="datetime-local"
                class="form-control"
                name="changeDate"
                :class="{ valid: !v$.changeDate.$invalid, invalid: v$.changeDate.$invalid }"
                required
                :value="convertDateTimeFromServer(v$.changeDate.$model)"
                @change="updateInstantField('changeDate', $event)"
              />
            </div>
            <div v-if="v$.changeDate.$anyDirty && v$.changeDate.$invalid">
              <small class="form-text text-danger" v-for="error of v$.changeDate.$errors" :key="error.$uid">{{ error.$message }}</small>
            </div>
          </div>
        </div>
        <div>
          <button type="button" id="cancel-save" data-cy="entityCreateCancelButton" class="btn btn-secondary" v-on:click="previousState()">
            <font-awesome-icon icon="ban"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.cancel')"></span>
          </button>
          <button
            type="submit"
            id="save-entity"
            data-cy="entityCreateSaveButton"
            :disabled="v$.$invalid || isSaving"
            class="btn btn-primary"
          >
            <font-awesome-icon icon="save"></font-awesome-icon>&nbsp;<span v-text="t$('entity.action.save')"></span>
          </button>
        </div>
      </form>
    </div>
  </div>
</template>
<script lang="ts" src="./trx-product-history-update.component.ts"></script>
